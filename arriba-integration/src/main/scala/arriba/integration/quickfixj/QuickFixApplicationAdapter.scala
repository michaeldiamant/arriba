package arriba.integration.quickfixj
import quickfix.Message
import quickfix.SessionID
import java.util.concurrent.atomic.AtomicInteger
import quickfix.Application

class QuickFixApplicationAdapter(messageProcessors: Seq[PartialFunction[Message, Unit]]) extends Application {

  private val responseCounter = new AtomicInteger

  override def fromApp(message: Message, sessionId: SessionID) {
    messageProcessors(responseCounter.getAndIncrement)(message)
  }

  override def fromAdmin(message: Message, sessionId: SessionID) {
    messageProcessors(responseCounter.getAndIncrement)(message)
  }

  override def toApp(message: Message, sessionId: SessionID) {}

  override def toAdmin(message: Message, sessionId: SessionID) {}

  override def onLogout(sessionId: SessionID) {}

  override def onLogon(sessionId: SessionID) {}

  override def onCreate(sessionId: SessionID) {}
}
