package arriba.scala.server

import java.util.concurrent.{Executors}
import org.jboss.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress
import arriba.scala.fix.session.{AlwaysResolvingSessionResolver, SessionResolver}
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import arriba.scala.fix.netty.{FixMessageFrameDecoder, SerializedFieldHandler}
import org.jboss.netty.channel._
import arriba.scala.fix.disruptor.{FixMessageEntry, SerializedFieldsToRingBufferEntryAdapter, SessionNotifyingFixMessageEntryBatchHandler}
import com.lmax.disruptor._
import arriba.scala.fix.SerializedField
import arriba.scala.senders.RingBufferSender
import arriba.scala.common.Sender

class FixServer {
  def start() {
    val ringBuffer = new RingBuffer[FixMessageEntry](new FixMessageEntryFactory, 1024 * 32,
      ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.BUSY_SPIN)
    val consumerBarrier = ringBuffer.createConsumerBarrier()
    val consumer = new BatchConsumer[FixMessageEntry](consumerBarrier, new SessionNotifyingFixMessageEntryBatchHandler(this.sessionResolver))
    val executorService = Executors.newSingleThreadExecutor
    executorService.submit(consumer)
    val producerBarrier = ringBuffer.createProducerBarrier(consumer)
    val bootstrap = this.server(producerBarrier)
    bootstrap.bind(new InetSocketAddress(8080))
  }

  private def sessionResolver: SessionResolver = {
    new AlwaysResolvingSessionResolver
  }

  private def server(producerBarrier: ProducerBarrier[FixMessageEntry]): ServerBootstrap = {
    val factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool)
    val bootstrap = new ServerBootstrap(factory)
    val deserializedFixMessageHandler = new SerializedFieldHandler(this.ringBufferSender(producerBarrier))
    bootstrap.setPipelineFactory(new ChannelPipelineFactory {
      def getPipeline = Channels.pipeline(new FixMessageFrameDecoder, deserializedFixMessageHandler)
    })
    bootstrap.setOption("child.tcpNoDelay", true)
    bootstrap.setOption("child.keepAlive", true)
    bootstrap
  }

  private def ringBufferSender(producerBarrier:ProducerBarrier[FixMessageEntry]):Sender[List[SerializedField]] = {
         new RingBufferSender[List[SerializedField], FixMessageEntry](producerBarrier,
                new SerializedFieldsToRingBufferEntryAdapter())
    }
}

