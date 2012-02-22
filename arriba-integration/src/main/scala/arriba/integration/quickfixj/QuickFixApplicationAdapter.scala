package arriba.integration.quickfixj
import java.util.concurrent.atomic.AtomicInteger
import quickfix.field.{Password, Username, MsgType}
import quickfix.{Session, Message, SessionID, Application}

class QuickFixApplicationAdapter(messageProcessors: Seq[PartialFunction[Message, Unit]],
                                 sessionOption: Option[FixSession] = None) extends Application {

  private val responseCounter = new AtomicInteger

  override def fromApp(message: Message, sessionId: SessionID) {
    messageProcessors(responseCounter.getAndIncrement)(message)
  }

  override def fromAdmin(message: Message, sessionId: SessionID) {
    messageProcessors(responseCounter.getAndIncrement)(message)
  }

  override def toApp(message: Message, sessionId: SessionID) {}

  override def toAdmin(message: Message, sessionId: SessionID) {
    if (message.getHeader.getString(MsgType.FIELD) == MsgType.LOGON) {
      sessionOption match {
        case Some(session) => {
          message.setString(Username.FIELD, session.username)
          message.setString(Password.FIELD, session.password)
        }
        case None => // Nothing to do
      }
    }
  }

  override def onLogout(sessionId: SessionID) {}

  override def onLogon(sessionId: SessionID) {}

  override def onCreate(sessionId: SessionID) {}
}
