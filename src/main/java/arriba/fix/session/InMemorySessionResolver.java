package arriba.fix.session;

import java.util.Map;

public final class InMemorySessionResolver implements SessionResolver {

    private final Map<SessionId, Session> sessionIdToSession;

    public InMemorySessionResolver(final Map<SessionId, Session> sessionIdToSession) {
        this.sessionIdToSession = sessionIdToSession;
    }

    public Session resolve(final SessionId sessionId) throws UnknownSessionIdException {
        final Session session = this.sessionIdToSession.get(sessionId);
        if (session == null) {
            throw new UnknownSessionIdException("Unknown session ID:  " + sessionId + ".");
        }

        return session;
    }
}
