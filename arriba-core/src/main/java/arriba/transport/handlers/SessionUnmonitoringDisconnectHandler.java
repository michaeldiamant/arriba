package arriba.transport.handlers;

import arriba.fix.session.SessionId;
import arriba.fix.session.SessionMonitor;
import arriba.transport.TransportIdentity;
import arriba.transport.TransportRepository;

public final class SessionUnmonitoringDisconnectHandler<T> implements TransportDisconnectHandler<T> {

    private final SessionMonitor monitor;
    private final TransportRepository<SessionId, T> repository;

    public SessionUnmonitoringDisconnectHandler(SessionMonitor monitor, TransportRepository<SessionId, T> repository) {
        this.monitor = monitor;
        this.repository = repository;
    }

    @Override
    public void onDisconnect(TransportIdentity<T> identity) {
        final SessionId sessionId = repository.find(identity);
        if (null != sessionId) {
            monitor.unmonitor(sessionId);
        }
    }
}
