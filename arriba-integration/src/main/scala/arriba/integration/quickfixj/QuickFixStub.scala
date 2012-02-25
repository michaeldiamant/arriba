package arriba.integration.quickfixj

import arriba.configuration.ArribaWizardType
import scala.PartialFunction
import arriba.configuration.DisruptorConfiguration
import com.lmax.disruptor.MultiThreadedClaimStrategy
import com.lmax.disruptor.BlockingWaitStrategy
import arriba.transport.InMemoryTransportRepository
import arriba.configuration.ArribaWizard
import arriba.fix.Tags
import arriba.fix.fields.MessageType
import arriba.fix.inbound.messages.InboundFixMessage
import arriba.fix.inbound.messages.InboundFixMessageFactory
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier
import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository
import arriba.common.Handler
import arriba.transport.netty.bootstraps.FixServerBootstrap
import arriba.transport.netty.bootstraps.FixClientBootstrap
import java.net.InetSocketAddress
import java.util.concurrent.{CountDownLatch, Executors}
import arriba.transport.netty._
import collection.mutable.{ListBuffer, ArrayBuffer}
import arriba.integration.runner.ClientWizard
import quickfix._
import com.weiglewilczek.slf4s.Logging
import org.jboss.netty.channel._
import group.DefaultChannelGroup
import arriba.fix.session.SessionId
import arriba.transport.handlers.{SessionUnmonitoringDisconnectHandler, LogonOnConnectHandler}

case class FixSession(beginString: String, senderCompId: String, targetCompId: String, username: String, password: String)

trait FixClientStub {

  val clientType: ClientType

  def start()

  def stop()
}

class QuickFixStub(val clientType: ClientType) extends FixClientStub {

  private val handlers = new ArrayBuffer[PartialFunction[Message, Unit]]
  private val sessions = ArrayBuffer[FixSession]()

  def addSession(session: FixSession) = {
    sessions += session
    this
  }

  var initiatorOption: Option[SocketInitiator] = None
  var acceptorOption: Option[SocketAcceptor] = None

  def start() {
    clientType match {
      case Acceptor => {
        val settings = sessions.foldLeft(new SessionSettings())((settings, session) => SessionSettingsFactory.newAcceptor(session.beginString, session.senderCompId, session.targetCompId, settings))

        val acceptor = new SocketAcceptor(
          new QuickFixApplicationAdapter(handlers),
          new FileStoreFactory(settings),
          settings,
          new FileLogFactory(settings),
          new DefaultMessageFactory)

        acceptor.start()
        acceptorOption = Some(acceptor)
      }
      case Initiator => {
        val settings = sessions.foldLeft(new SessionSettings())((settings, session) => SessionSettingsFactory.newInitiator(session.beginString, session.senderCompId, session.targetCompId, settings))
        val initiator = new SocketInitiator(
          new QuickFixApplicationAdapter(handlers, Some(sessions.head)),
          new MemoryStoreFactory,
          settings,
          new FileLogFactory(settings),
          new DefaultMessageFactory)

        initiator.start()
        initiatorOption = Some(initiator)
      }
    }
  }

  def send(message: Message)(implicit wizard: ClientWizard) {
    wizard += (latch => {
      () => {
        Session.sendToTarget(message)
        latch.countDown()
      }
    })
  }

  def stop() {
    (initiatorOption, acceptorOption) match {
      case (Some(initiator), None) => initiator.stop(true)
      case (None, Some(acceptor)) => acceptor.stop(true)
      case (None, None) => throw new IllegalStateException()
    }
  }

  def waitForLogon()(implicit wizard: ClientWizard) {
    wizard += (latch => {
      () => {
        while (!initiatorOption.get.isLoggedOn) {
          Thread.sleep(200)
        }
        latch.countDown()
      }
    })
  }

  def handle(pf: PartialFunction[Message, Unit])(implicit wizard: ClientWizard) {
    wizard += (latch => {
      handlers += pf.andThen(message => latch.countDown())

      null
    })
  }
}

class ArribaStub(val clientType: ClientType)
  extends FixClientStub
  with Logging {

  private val inboundConfiguration = new DisruptorConfiguration(
    Executors.newCachedThreadPool,
    new MultiThreadedClaimStrategy(256),
    new BlockingWaitStrategy
  )
  private val outboundConfiguration = new DisruptorConfiguration(
    Executors.newCachedThreadPool,
    new MultiThreadedClaimStrategy(256),
    new BlockingWaitStrategy
  )

  private val backingRepository = new InMemoryTransportRepository[SessionId, Channel](new NettyTransportFactory())
  val repository = new NettyTransportRepository(backingRepository);

  val wizardType = clientType match {
    case Acceptor => ArribaWizardType.ACCEPTOR
    case Initiator => ArribaWizardType.INITIATOR
  }

  lazy val wizard = new ArribaWizard(wizardType, inboundConfiguration, outboundConfiguration, repository)

  val headerBuilder = new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository).getHeaderBuilder
  val factory = new InboundFixMessageFactory

  val messageTemplates = MessageType.values.map(messageType =>
    factory.create(headerBuilder.addField(Tags.MESSAGE_TYPE, messageType.getSerializedValue).build, null, null, null))

  // Kludge
  private val sessions = ListBuffer[FixSession]()

  def addSession(session: FixSession) = {
    sessions += session
    wizard.register(session.senderCompId).`with`(session.targetCompId)
    this
  }

  def stop() {
    group.unbind().awaitUninterruptibly()
    channelFactory.foreach(_.releaseExternalResources())

    wizard.stop()
  }

  var channelFactory: Option[ChannelFactory] = None
  var group = new DefaultChannelGroup("myid")

  def start() {

    wizard.start()

    clientType match {
      case Acceptor => {
        val bootstrap = FixServerBootstrap.create(new FixMessageFrameDecoder(),
          new NettyDisconnectHandlerAdapter(new SessionUnmonitoringDisconnectHandler(wizard.getSessionMonitor, repository)),
          new GroupAddingChannelHandler(group),
          new SerializedFixMessageHandler(wizard.getInboundSender)
        )

        val channel = bootstrap.bind(new InetSocketAddress("localhost", 8080))
        channelFactory = Some(channel.getFactory)
        group.add(channel)
      }
      case Initiator => {
        val session = sessions.head

        val bootstrap = FixClientBootstrap.create(
          new FixMessageFrameDecoder,
          new NettyConnectHandlerAdapter(new LogonOnConnectHandler[Channel](
            session.senderCompId,
            session.targetCompId,
            30,
            session.username,
            session.password,
            wizard.getOutboundSender,
            repository,
            wizard.createOutboundBuilder())),
          new NettyDisconnectHandlerAdapter(new SessionUnmonitoringDisconnectHandler(wizard.getSessionMonitor, repository)),
          new SerializedFixMessageHandler(wizard.getInboundSender)
        );

        val channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8080))
        channelFuture.addListener(new ChannelFutureListener {
          def operationComplete(future: ChannelFuture) {
              channelFactory = Some(future.getChannel.getFactory)
              group.add(future.getChannel)
          }
        })
      }
    }
  }

  def handle(pf: PartialFunction[InboundFixMessage, Unit])(implicit clientWizard: ClientWizard) {
    var pfLatch: CountDownLatch = null
    clientWizard += (latch => {
      pfLatch = latch

      null
    })

    messageTemplates.find(message => pf.isDefinedAt(message)) match {
      case Some(message) => wizard.registerMessageHandler(MessageType.toMessageType(message.getMessageType),
        new Handler[InboundFixMessage]() {

          def handle(message: InboundFixMessage) {
            pf.andThen(message => pfLatch.countDown())(message)
          }
        })
      case None => throw new IllegalArgumentException("No matching messages")
    }
  }
}

object QuickFixStub {

  def newAcceptor(beginString: String, senderCompId: String, targetCompId: String) = new QuickFixStub(Acceptor)

  def newInitiator(beginString: String, senderCompId: String, targetCompId: String) = new QuickFixStub(Initiator)
}

sealed trait ClientType

object Acceptor extends ClientType

object Initiator extends ClientType
