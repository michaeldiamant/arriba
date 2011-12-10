package arriba.disruptor.outbound;

import arriba.common.Handler;
import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionResolver;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

public final class TransportWritingFixMessageHandler<T> implements Handler<OutboundFixMessage> {

    private final TransportRepository<String, T> transportRepository;
    private final SessionResolver sessionResolver;

    public TransportWritingFixMessageHandler(final TransportRepository<String, T> transportRepository,
            final SessionResolver sessionResolver) {
        this.transportRepository = transportRepository;
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void handle(final OutboundFixMessage message) {
        final Transport<T> transport = this.transportRepository.find(message.getTargetCompId());
        if (null == transport) {
            throw new IllegalArgumentException("Cannot find transport for target comp ID " + message.getTargetCompId() + ".");
        }

        // TODO Can SessionId be cached?
        final Session session = this.sessionResolver.resolve(new SessionId(message.getSenderCompId(), message.getTargetCompId()));
        if (null == session) {
            throw new IllegalArgumentException("Cannot find session for sender comp ID " + message.getSenderCompId() +
                    " and target comp ID " + message.getTargetCompId() + ".");
        }

        transport.write(message.toBytes(session.getNextSequenceNumber(), DateSupplier.getUtcTimestamp()));

        session.updateLastSentTimestamp();
    }
}
