package arriba.scala.common

import collection.mutable.HashMap

final class MapHandlerRepository[ID, M](messageIdentifierToHandler: HashMap[ID, Handler[M]]) extends HandlerRepository[ID, M] {

  def registerHandler(messageIdentifier: ID, handler: Handler[M]) {
    messageIdentifierToHandler += (messageIdentifier -> handler)
  }

  def findHandler(messageIdentifier: ID): Handler[M] = {
    messageIdentifierToHandler.get(messageIdentifier) match {
      case Some(handler) => handler
      //TODO perhaps handle this better now that we have an optoin pattern instead of java null
      case _ =>  throw new Exception("Provided identifier " + messageIdentifier + " does not map to a known handler.")
    }
  }
}

