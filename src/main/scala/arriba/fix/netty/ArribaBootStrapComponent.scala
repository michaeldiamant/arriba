package arriba.fix.netty

import org.specs2._
import org.jboss.netty.buffer.ChannelBuffers
import java.nio.charset.Charset
import org.jboss.netty.bootstrap.{ServerBootstrap, ClientBootstrap}
import java.net.InetSocketAddress
import java.lang.Thread
import org.jboss.netty.channel._
import socket.nio.{NioServerSocketChannelFactory, NioClientSocketChannelFactory}
import java.util.concurrent.{Executor, Executors}


trait ArribaBootStrapComponent {
  def arribaBootStrap: ArribaBootStrap
  trait ArribaBootStrap {
    def boss: Executor
    def worker: Executor
    def startClients(numClients:Int, server: Channel)
    def startServer(port:Int)
  }
}






