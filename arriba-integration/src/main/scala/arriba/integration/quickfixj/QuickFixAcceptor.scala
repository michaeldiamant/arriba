package arriba.integration.quickfixj
import quickfix.SessionSettings
import quickfix.SocketAcceptor
import quickfix.FileStoreFactory
import quickfix.FileLogFactory
import quickfix.DefaultMessageFactory
import quickfix.SessionID

class QuickFixAcceptor {

  val settings = new SessionSettings

  val id = new SessionID("FIX.4.4", "MM", "MT")

  settings.setString(id, "BeginString", id.getBeginString())
  settings.setString(id, "SenderCompID", id.getSenderCompID())
  settings.setString(id, "TargetCompID", id.getTargetCompID())
  settings.setString(id, "ConnectionType", "acceptor")
  settings.setString(id, "StartTime", "00:00:00")
  settings.setString(id, "EndTime", "00:00:00")
  settings.setString(id, "TimeZone", "America/New_York")

  settings.setLong(id, "SocketAcceptPort", 8080)
  settings.setString(id, "SocketAcceptAddress", "localhost")

  settings.setString(id, "FileLogPath", "/tmp")
  settings.setString(id, "FileStorePath", "/tmp")

  val acceptor = new SocketAcceptor(
    new QuickFixApplication,
    new FileStoreFactory(settings),
    settings,
    new FileLogFactory(settings),
    new DefaultMessageFactory)

  acceptor.start()

  while (true) {}
}

object QuickFixAcceptor {

  def main(args: Array[String]) {
    new QuickFixAcceptor
  }
}