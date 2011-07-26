package arriba.scala.server

import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}
import java.nio.charset.Charset
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import java.util.concurrent.Executors
import org.jboss.netty.bootstrap.ClientBootstrap
import java.net.InetSocketAddress
import org.jboss.netty.channel._

object NettyInitiator {
  private def writeContinuouslyForMs(channel: Channel, durationInMs: Long) {
    val startTime = System.currentTimeMillis
    var msgCount = 0
    while (System.currentTimeMillis - startTime <= durationInMs) {
      val rawFixMsg = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=" + System.currentTimeMillis + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001"
      val buffer = ChannelBuffers.copiedBuffer(rawFixMsg, Charset.defaultCharset)
      channel.write(buffer)
      ({
        msgCount += 1; msgCount - 1
      })
    }
    println(">>> sent " + msgCount + "msgs")
  }

  private def writeMsgAtOnce(channel: Channel) {
    val rawFixMsg: String = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17" + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001"
    val buffer: ChannelBuffer = ChannelBuffers.copiedBuffer(rawFixMsg, Charset.defaultCharset)
    channel.write(buffer)
  }

  private def writeMsgWithSplit(channel: Channel) {
    val part1Msg = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17"
    val buffer = ChannelBuffers.copiedBuffer(part1Msg, Charset.defaultCharset)
    channel.write(buffer)
    Thread.sleep(1000)
    val part2Msg = "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001"
    val buffer2 = ChannelBuffers.copiedBuffer(part2Msg, Charset.defaultCharset)
    channel.write(buffer2)
  }

  def main(args: Array[String]) {
    new NettyInitiator
  }
}

class NettyInitiator {
    val factory: ChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool)
    val bootstrap: ClientBootstrap = new ClientBootstrap(factory)
    bootstrap.setPipelineFactory(new ChannelPipelineFactory {
      def getPipeline: ChannelPipeline = {
        Channels.pipeline(new SimpleChannelHandler {
          override def channelConnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) {
            val channel: Channel = e.getChannel
            NettyInitiator.writeMsgAtOnce(channel)
          }
        })
      }
    })
    bootstrap.setOption("tcpNoDelay", true)
    bootstrap.setOption("keepAlive", true)
    bootstrap.connect(new InetSocketAddress("localhost", 8080))
}

