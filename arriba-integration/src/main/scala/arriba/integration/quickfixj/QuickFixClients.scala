package arriba.integration.quickfixj

import quickfix.SocketInitiator
import quickfix.FileStoreFactory
import quickfix.FileLogFactory
import quickfix.DefaultMessageFactory
import quickfix.SocketAcceptor
import QuickFixClients._

object QuickFixClients {

  val beginString = "FIX.4.4"
  val marketTakerCompId = "MT"
  val marketMakerCompId = "MM"
}

class QuickFixInitiator {
  val settings = SessionSettingsFactory.newInitiator(beginString, marketTakerCompId, marketMakerCompId)

  val initiator = new SocketInitiator(
    new InitiatorApplication,
    new FileStoreFactory(settings),
    settings,
    new FileLogFactory(settings),
    new DefaultMessageFactory)

  initiator.start()

  while (true) {}
}

class QuickFixAcceptor {
  val settings = SessionSettingsFactory.newAcceptor(beginString, marketMakerCompId, marketTakerCompId)

  val acceptor = new SocketAcceptor(
    new QuickFixApplication,
    new FileStoreFactory(settings),
    settings,
    new FileLogFactory(settings),
    new DefaultMessageFactory)

  acceptor.start()

  while (true) {}
}

object QuickFixInitiator {

  def main(args: Array[String]) {
    new QuickFixInitiator
  }
}

object QuickFixAcceptor {

  def main(args: Array[String]) {
    new QuickFixAcceptor
  }
}
