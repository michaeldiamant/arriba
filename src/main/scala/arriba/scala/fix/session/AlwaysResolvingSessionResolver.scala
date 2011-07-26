package arriba.scala.fix.session

import collection.mutable.HashMap
import arriba.scala.fix.messages.FixMessage
import arriba.scala.common.{MapHandlerRepository, PrintingHandler, Handler}

class AlwaysResolvingSessionResolver extends SessionResolver {

  val messageTypeToHandler = new HashMap[String, Handler[FixMessage]]
  messageTypeToHandler.put("D", new PrintingHandler[FixMessage])
  val session = new Session(new SimpleSessionId("targetCompId"),
    new MapHandlerRepository[String, FixMessage](messageTypeToHandler))

  def resolve(sessionId: SessionId): Session = {
    session
  }
}