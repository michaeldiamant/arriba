package arriba.scala.fix.session

import collection.mutable.HashMap
import arriba.scala.fix.messages.FixMessage
import arriba.scala.common.Handler

class AlwaysResolvingSessionResolver extends SessionResolver {

  val messageTypeToHandler = new HashMap[String, Handler[FixMessage]]
  messageTypeToHandler.put("D", new Nothing)
  val session = new Session(new SimpleSessionId("targetCompId"), new Nothing(messageTypeToHandler))

  def resolve(sessionId: SessionId): Session = {
    session
  }
}

