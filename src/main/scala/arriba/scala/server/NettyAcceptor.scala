package arriba.scala.server

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.util.concurrent.Executors
import org.jboss.netty.bootstrap.ServerBootstrap
import arriba.fix.netty.{FixMessageHandler, FixMessageFrameDecoder}
import java.net.InetSocketAddress
import org.jboss.netty.channel.{ChannelPipelineFactory, Channels, ChannelPipeline, ChannelFactory}
import arriba.fix.messages.FixMessage
import arriba.senders.VoidSender
import arriba.common.Sender

object NettyAcceptor {
  def main(args: Array[String]) {
    new NettyAcceptor
  }
}

class NettyAcceptor {
  val factory: ChannelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool)
  val bootstrap: ServerBootstrap = new ServerBootstrap(factory)
  bootstrap.setPipelineFactory(new ChannelPipelineFactory {
    def getPipeline: ChannelPipeline = {
      Channels.pipeline(new FixMessageFrameDecoder, new FixMessageHandler(ringBufferSender))
    }
  })
  bootstrap.setOption("child.tcpNoDelay", true)
  bootstrap.setOption("child.keepAlive", true)
  bootstrap.bind(new InetSocketAddress(8080))

  private def ringBufferSender: Sender[FixMessage] = {
    new VoidSender[FixMessage]
  }

}

