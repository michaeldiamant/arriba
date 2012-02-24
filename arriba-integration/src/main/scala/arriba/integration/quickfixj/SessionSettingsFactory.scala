package arriba.integration.quickfixj
import quickfix.SessionSettings
import quickfix.SessionID

object SessionSettingsFactory {

  def newInitiator(beginString: String, senderCompId: String, targetCompId: String, settings: SessionSettings = new SessionSettings) =
    createSettings(beginString, senderCompId, targetCompId, settings, "initiator")

  def newAcceptor(beginString: String, senderCompId: String, targetCompId: String, settings: SessionSettings = new SessionSettings) =
    createSettings(beginString, senderCompId, targetCompId, settings, "acceptor")

  private def createSettings(beginString: String, senderCompId: String, targetCompId: String,
    settings: SessionSettings, connectionType: String): SessionSettings = {
    val id = new SessionID(beginString, senderCompId, targetCompId)

    settings.setString(id, "BeginString", id.getBeginString)
    settings.setString(id, "SenderCompID", id.getSenderCompID)
    settings.setString(id, "TargetCompID", id.getTargetCompID)

    settings.setString(id, "ConnectionType", connectionType)
    settings.setString(id, "StartTime", "00:00:00")
    settings.setString(id, "EndTime", "00:00:00")
    settings.setString(id, "TimeZone", "America/New_York")

    connectionType match {
      case "initiator" => {
        settings.setLong(id, "SocketConnectPort", 8080)
        settings.setString(id, "SocketConnectHost", "localhost")
        settings.setLong(id, "HeartBtInt", 30)
        settings.setLong(id, "ReconnectInterval", 5)
      }
      case "acceptor" => {
        settings.setLong(id, "SocketAcceptPort", 8080)
        settings.setString(id, "SocketAcceptAddress", "localhost")
      }
    }

    settings.setString(id, "FileLogPath", "/tmp")
    settings.setString(id, "FileStorePath", "/tmp")

    settings
  }
}