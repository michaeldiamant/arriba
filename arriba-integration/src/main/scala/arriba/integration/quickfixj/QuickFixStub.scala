package arriba.integration.quickfixj
import quickfix.Message
import arriba.configuration.ArribaWizardType
import arriba.transport.netty.NewClientSessionHandler
import arriba.transport.handlers.LogonOnConnectHandler
import quickfix.Session
import quickfix.SessionSettings
import scala.PartialFunction
import scala.collection.mutable.ArrayBuffer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import quickfix.SocketInitiator
import quickfix.FileStoreFactory
import quickfix.FileLogFactory
import quickfix.DefaultMessageFactory
import quickfix.SocketAcceptor
import arriba.configuration.DisruptorConfiguration
import java.util.concurrent.Executors
import com.lmax.disruptor.MultiThreadedClaimStrategy
import com.lmax.disruptor.BlockingWaitStrategy
import arriba.transport.InMemoryTransportRepository
import arriba.transport.netty.SerializedFixMessageHandler
import arriba.transport.netty.NettyConnectHandlerAdapter
import org.jboss.netty.channel.Channel
import arriba.transport.netty.NettyTransportFactory
import arriba.transport.netty.NettyTransportRepository
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
import arriba.transport.netty.FixMessageFrameDecoder
import java.net.InetSocketAddress

case class FixSession(beginString: String, senderCompId: String, targetCompId: String, username: String, password: String)

trait FixClientStub {

  protected val actions = new ArrayBuffer[() => Unit]
  protected val handlers = new ArrayBuffer[PartialFunction[Message, Unit]]
}

class QuickFixStub(client: ClientType) extends FixClientStub {

  private val sessions = ArrayBuffer[FixSession]()

  def addSession(session: FixSession) = {
    sessions += session
    this
  }

  def start() {
    client match {
      case Acceptor => {
        val settings = sessions.foldLeft(new SessionSettings())((settings, session) => SessionSettingsFactory.newAcceptor(session.beginString, session.senderCompId, session.targetCompId, settings))

        val acceptor = new SocketAcceptor(
          new QuickFixApplicationAdapter(handlers),
          new FileStoreFactory(settings),
          settings,
          new FileLogFactory(settings),
          new DefaultMessageFactory)

        acceptor.start()
      }
      case Initiator => {
        val settings = sessions.foldLeft(new SessionSettings())((settings, session) => SessionSettingsFactory.newInitiator(session.beginString, session.senderCompId, session.targetCompId, settings))
        val initiator = new SocketInitiator(
          new QuickFixApplicationAdapter(handlers),
          new FileStoreFactory(settings),
          settings,
          new FileLogFactory(settings),
          new DefaultMessageFactory)
        
        initiator.start()
        while (!initiator.isLoggedOn) {
          Thread.sleep(200)
        }
      }
    }
  }

  def send(message: Message) {
    actions += (() => Session.sendToTarget(message))
  }

  def handle(pf: PartialFunction[Message, Unit]) {
    val latch = new CountDownLatch(1)

    handlers += pf.andThen(message => latch.countDown())

    actions += (() => {
      latch.await(1, TimeUnit.SECONDS) match {
        case true => // OK
        case false => // Failure
      }
    })
  }
}

class ArribaStub(client: ClientType) extends FixClientStub {

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

  private val backingRepository = new InMemoryTransportRepository[String, Channel](new NettyTransportFactory())
  private val repository = new NettyTransportRepository(backingRepository);

  val wizardType = client match {
    case Acceptor => ArribaWizardType.ACCEPTOR
    case Initiator => ArribaWizardType.INITIATOR
  }

  private val wizard = new ArribaWizard(wizardType, inboundConfiguration, outboundConfiguration, repository)

  val headerBuilder = new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository()).getHeaderBuilder()
  val factory = new InboundFixMessageFactory

  val messageTemplates = MessageType.values.map(messageType =>
    factory.create(headerBuilder.addField(Tags.MESSAGE_TYPE, messageType.getSerializedValue).build, null, null, null))

  def addSession(session: FixSession) = {
    wizard.register(session.senderCompId).`with`(session.targetCompId)
    this
  }

  def start() {
    wizard.start()

    client match {
      case Acceptor => {
        val acceptor = FixServerBootstrap.create(
          new FixMessageFrameDecoder(),
          new NewClientSessionHandler(null),
          new SerializedFixMessageHandler(null)
        )

        acceptor.bind(new InetSocketAddress("localhost", 8080));
      }
      case Initiator => {
        // FIXME initiator initialize incomplete
        val initiator = FixClientBootstrap.create(
                new FixMessageFrameDecoder(),
                null,
                null
//                new NettyConnectHandlerAdapter(new LogonOnConnectHandler[Channel](this.senderCompId, this.targetCompId, this.heartbeatIntervalInMs, this.username, this.password, outboundSender, repository, wizard.createOutboundBuilder(), wizard.getSessionMonitor())),
//                new SerializedFixMessageHandler(inboundSender)
                );

        initiator.connect(new InetSocketAddress("localhost", 8080));
      }
    }
  }

  def handle(pf: PartialFunction[InboundFixMessage, Unit]) {
    messageTemplates.find(message => pf.isDefinedAt(message)) match {
      case Some(message) => wizard.registerMessageHandler(MessageType.toMessageType(message.getMessageType),
        new Handler[InboundFixMessage]() {

          def handle(message: InboundFixMessage) {
            pf(message)
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

protected sealed trait ClientType
object Acceptor extends ClientType
object Initiator extends ClientType
