package arriba.fix.session.disconnect;

import arriba.fix.session.LogoutTracker;
import arriba.fix.session.SessionId;

public final class LogoutMarkClearingDisconnectListener implements SessionDisconnectListener {

    private final LogoutTracker tracker;

    public LogoutMarkClearingDisconnectListener(final LogoutTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void onDisconnect(final SessionId sessionId) {
        this.tracker.clearMark(sessionId);
    }
}
