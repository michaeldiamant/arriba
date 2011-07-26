package arriba.scala.fix.session

import arriba.scala.common.HandlerRepository
import arriba.scala.fix.messages.{FixMessageFactory, FixMessage}

class Session(sessionId: SessionId, messageHandlerRepository: HandlerRepository[String, _ <: FixMessage])  {

  def onMessage[T <: FixMessage](fixMessage:T) {
    try {
      val handler = messageHandlerRepository.findHandler(fixMessage.getMessageType).asInstanceOf[T]
    }
    catch {
      case e => {
        throw new Exception(e)
      }
    }
    //TODO manual intervention required
    //handler.handle(fixMessage)
  }

  def getSessionId: SessionId = {
    sessionId
  }

}

