package arriba.fix.session;

import java.util.Map;

public final class InMemorySessionResolver implements SessionResolver {

    private final Map<SessionId, Session> sessionIdToSession;

    public InMemorySessionResolver(final Map<SessionId, Session> sessionIdToSession) {
        this.sessionIdToSession = sessionIdToSession;
    }

    public Session resolve(final SessionId sessionId) {
        final Session session = this.sessionIdToSession.get(sessionId);
        if (null == session) {
            throw new IllegalArgumentException("Cannot find session for sender comp ID " + sessionId.getSenderCompId() +
                    " and target comp ID " + sessionId.getTargetCompId() + ".");
        }

        return session;
    }
}
