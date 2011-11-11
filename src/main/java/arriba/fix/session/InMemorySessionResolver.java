package arriba.fix.session;

import java.util.Map;

public final class InMemorySessionResolver implements SessionResolver {

    private final Map<SessionId, Session> sessionIdToSession;

    public InMemorySessionResolver(final Map<SessionId, Session> sessionIdToSession) {
        this.sessionIdToSession = sessionIdToSession;
    }

    public Session resolve(final SessionId sessionId) {
        return this.sessionIdToSession.get(sessionId);
    }
}
