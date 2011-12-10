package arriba.fix.session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InMemoryLogoutTracker implements LogoutTracker {

    private final Set<SessionId> markedSessionIds = Collections.synchronizedSet(new HashSet<SessionId>());

    public boolean markLogout(final SessionId sessionId) {
        return this.markedSessionIds.add(sessionId);
    }

    public boolean clearMark(final SessionId sessionId) {
        return this.markedSessionIds.remove(sessionId);
    }

    public boolean hasIssuedLogout(final SessionId sessionId) {
        return this.markedSessionIds.contains(sessionId);
    }
}
