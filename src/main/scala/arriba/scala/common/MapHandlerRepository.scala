package arriba.scala.common

import arriba.common.NonexistentHandlerException

final class MapHandlerRepository[ID, M] extends HandlerRepository[ID, M] {
  def this(messageIdentifierToHandler: Map[ID, Handler[M]]) {
    this ()
    this.messageIdentifierToHandler = messageIdentifierToHandler
  }

  def registerHandler(messageIdentifier: ID, handler: Handler[M]) {
    messageIdentifierToHandler += (messageIdentifier -> handler)
  }

  def findHandler(messageIdentifier: ID): Handler[M] = {
    messageIdentifierToHandler.get(messageIdentifier) match {
      case Some(handler) => handler
      //TODO perhaps handle this better now that we have an optoin pattern instead of java null
      case _ =>  throw new NonexistentHandlerException("Provided identifier " + messageIdentifier + " does not map to a known handler.")
    }
  }

  private var messageIdentifierToHandler: Map[ID, Handler[M]] = null
}

