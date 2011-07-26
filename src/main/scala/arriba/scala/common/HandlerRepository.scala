package arriba.scala.common

trait HandlerRepository[ID, M] {
  def registerHandler(identifier: ID, handler: Handler[M])

  def findHandler(identifier: ID): Handler[M]
}

