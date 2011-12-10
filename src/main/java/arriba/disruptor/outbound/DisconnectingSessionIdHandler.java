package arriba.disruptor.outbound;

import java.util.Set;

import arriba.common.Handler;
import arriba.fix.session.SessionId;
import arriba.fix.session.disconnect.SessionDisconnectListener;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

public final class DisconnectingSessionIdHandler<T> implements Handler<SessionId>{

    private final TransportRepository<String, T> transportRepository;
    private final Set<SessionDisconnectListener> disconnectListeners;

    public DisconnectingSessionIdHandler(final TransportRepository<String, T> transportRepository,
            final Set<SessionDisconnectListener> disconnectListeners) {
        this.transportRepository = transportRepository;
        this.disconnectListeners = disconnectListeners;
    }

    @Override
    public void handle(final SessionId sessionId) {
        for (final SessionDisconnectListener listener : this.disconnectListeners) {
            listener.onDisconnect(sessionId);
        }

        final Transport<T> transport = this.transportRepository.find(sessionId.getTargetCompId());
        if (null != transport) {
            transport.close();
        }
    }
}
