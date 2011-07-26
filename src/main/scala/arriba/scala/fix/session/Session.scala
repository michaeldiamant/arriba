package arriba.scala.fix.session

import arriba.fix.session.{SessionId, MessageHandlingException}
import arriba.scala.common.HandlerRepository
import arriba.scala.fix.messages.{FixMessageFactory, FixMessage}

class Session(sessionId: SessionId, messageHandlerRepository: HandlerRepository[String, _ <: FixMessage])  {

  def onMessage[T <: FixMessage](fixMessage:T) {
    var handler: T = null
    try {
      handler = messageHandlerRepository.findHandler(fixMessage.getMessageType).asInstanceOf[T]
    }
    catch {
      case e: Nothing => {
        throw new MessageHandlingException(e)
      }
    }
    //TODO manual intervention required
    //handler.handle(fixMessage)
  }

  def getSessionId: SessionId = {
    sessionId
  }

}

