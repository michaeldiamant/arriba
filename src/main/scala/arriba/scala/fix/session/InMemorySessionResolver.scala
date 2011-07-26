package arriba.scala.fix.session

import java.util.Map

final class InMemorySessionResolver(sessionIdToSession: Map[SessionId, Session]) extends SessionResolver {

  def resolve(sessionId: SessionId): Session = {
    val session: Session = this.sessionIdToSession.get(sessionId)
    if (session == null) {
      throw new Exception("Unknown session ID:  " + sessionId + ".")
    }
    session
  }
}

