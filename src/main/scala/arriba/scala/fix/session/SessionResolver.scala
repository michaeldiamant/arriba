package arriba.scala.fix.session

trait SessionResolver {
  def resolve(sessionId: SessionId): Session
}

