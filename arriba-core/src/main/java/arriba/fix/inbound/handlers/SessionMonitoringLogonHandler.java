package arriba.fix.inbound.handlers;

import arriba.common.Handler;
import arriba.fix.inbound.messages.Logon;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionMonitor;

public final class SessionMonitoringLogonHandler implements Handler<Logon> {

    private final SessionMonitor monitor;

    public SessionMonitoringLogonHandler(final SessionMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void handle(Logon message) {
        this.monitor.monitor(
                new SessionId(message.getTargetCompId(), message.getSenderCompId()),
                Integer.parseInt(message.getHeartbeatInterval()) * 1000
        );
    }
}
