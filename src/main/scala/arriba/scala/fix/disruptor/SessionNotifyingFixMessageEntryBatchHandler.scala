package arriba.scala.fix.disruptor

import arriba.scala.fix.session.{SessionResolver, Session}
import com.lmax.disruptor.BatchHandler
import arriba.scala.fix.messages.FixMessage

final class SessionNotifyingFixMessageEntryBatchHandler(sessionResolver: SessionResolver) extends BatchHandler[FixMessageEntry] {

  def onAvailable(entry: FixMessageEntry) {
    val fixMessage: FixMessage = entry.getFixMessage
    val session: Session = this.sessionResolver.resolve(fixMessage.getSessionId)
    session.onMessage(fixMessage)
  }

  def onEndOfBatch() {}
}

