package arriba.fix.session.disconnect;

import arriba.fix.session.SessionId;
import arriba.fix.session.SessionMonitor;

public final class SessionUnmonitoringDisconnectListener implements SessionDisconnectListener {

    private final SessionMonitor monitor;

    public SessionUnmonitoringDisconnectListener(final SessionMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void onDisconnect(final SessionId sessionId) {
        this.monitor.unmonitor(sessionId);
    }
}
