package arriba.scala.server

import java.util.concurrent.{Executors, ExecutorService}
import org.jboss.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress
import arriba.fix.session.{AlwaysResolvingSessionResolver, SessionResolver}
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import arriba.fix.netty.{FixMessageFrameDecoder, FixMessageHandler}
import arriba.fix.disruptor.{FixMessageEntry, FixMessageToRingBufferEntryAdapter, ReceivingFixMessageEntryBatchHandler, FixMessageEntryFactory}
import com.lmax.disruptor._
import org.jboss.netty.channel._
import arriba.common.Sender
import arriba.fix.messages.FixMessage
import arriba.senders.RingBufferSender

class FixServer {
  def start() {
    val ringBuffer = new RingBuffer[FixMessageEntry](new FixMessageEntryFactory, 1024 * 32,
      ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.BUSY_SPIN)
    val consumerBarrier = ringBuffer.createConsumerBarrier()
    val consumer = new BatchConsumer[FixMessageEntry](consumerBarrier, new ReceivingFixMessageEntryBatchHandler(this.sessionResolver))
    val executorService = Executors.newSingleThreadExecutor
    executorService.submit(consumer)
    val producerBarrier = ringBuffer.createProducerBarrier(consumer)
    val bootstrap = this.server(producerBarrier)
    bootstrap.bind(new InetSocketAddress(8080))
  }

  private def sessionResolver: SessionResolver = new AlwaysResolvingSessionResolver

  private def server(producerBarrier: ProducerBarrier[FixMessageEntry]): ServerBootstrap = {
    val factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool)
    val bootstrap = new ServerBootstrap(factory)
    val deserializedFixMessageHandler = new FixMessageHandler(this.ringBufferSender(producerBarrier))
    bootstrap.setPipelineFactory(new ChannelPipelineFactory {
      def getPipeline = Channels.pipeline(new FixMessageFrameDecoder, deserializedFixMessageHandler)
    })
    bootstrap.setOption("child.tcpNoDelay", true)
    bootstrap.setOption("child.keepAlive", true)
    bootstrap
  }

  private def ringBufferSender(producerBarrier: ProducerBarrier[FixMessageEntry]): Sender[FixMessage] = {
    new RingBufferSender[FixMessage, FixMessageEntry](producerBarrier, new FixMessageToRingBufferEntryAdapter)
  }
}

