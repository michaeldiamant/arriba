package arriba.scala.server

object Both {
  def main(args: Array[String]) {
    new FixServer().start()
    new NettyInitiator
  }
}


