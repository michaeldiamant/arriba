package arriba.disruptor.outbound;

import java.io.IOException;

import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionResolver;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

import com.lmax.disruptor.EventHandler;

public final class TransportWritingFixMessageEventHandler<T> implements EventHandler<OutboundFixMessageEvent> {

    private final TransportRepository<String, T> transportRepository;
    private final SessionResolver sessionResolver;

    public TransportWritingFixMessageEventHandler(final TransportRepository<String, T> transportRepository,
            final SessionResolver sessionResolver) {
        this.transportRepository = transportRepository;
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void onEvent(final OutboundFixMessageEvent entry, final boolean endOfBatch) throws Exception {
        if (null != entry.getFixMessage()) {
            this.writeFixMessage(entry.getFixMessage());
        }

        if (null != entry.getTargetCompId()) {
            this.disconnectSession(entry.getTargetCompId());
        }
    }

    private void disconnectSession(final String targetCompId) {
        final Transport<T> transport = this.transportRepository.find(targetCompId);
        if (null != transport) {
            transport.close();
        }
    }

    private void writeFixMessage(final OutboundFixMessage message) throws IOException {
        final Transport<T> transport = this.transportRepository.find(message.getTargetCompId());
        if (null == transport) {
            throw new IOException("Cannot find transport for target comp ID " + message.getTargetCompId() + ".");
        }

        // TODO Can SessionId be cached?
        final Session session = this.sessionResolver.resolve(new SessionId(message.getSenderCompId(), message.getTargetCompId()));
        if (null == session) {
            throw new IOException("Cannot find session for sender comp ID " + message.getSenderCompId() +
                    " and target comp ID " + message.getTargetCompId() + ".");
        }

        transport.write(message.toBytes(session.getNextSequenceNumber(), DateSupplier.getUtcTimestamp()));

        session.updateLastSentTimestamp();
    }
}
