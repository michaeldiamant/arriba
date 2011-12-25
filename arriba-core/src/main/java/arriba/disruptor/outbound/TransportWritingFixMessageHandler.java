package arriba.disruptor.outbound;

import arriba.common.Handler;
import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

public final class TransportWritingFixMessageHandler<T> implements Handler<OutboundEvent> {

    private final TransportRepository<String, T> transportRepository;
    private final SessionResolver sessionResolver;

    public TransportWritingFixMessageHandler(final TransportRepository<String, T> transportRepository,
            final SessionResolver sessionResolver) {
        this.transportRepository = transportRepository;
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void handle(final OutboundEvent event) {
        final OutboundFixMessage message = event.getFixMessage();
        final Transport<T> transport = this.transportRepository.find(message.getTargetCompId());
        if (null == transport) {
            throw new IllegalArgumentException("Cannot find transport for target comp ID " + message.getTargetCompId() + ".");
        }

        // TODO Can SessionId be cached?
        final Session session = this.sessionResolver.resolve(event.getSessionId());
        final int sequenceNumber = session.getNextOutboundSequenceNumber();
        final byte[] serializedMessage = message.toBytes(sequenceNumber, DateSupplier.getUtcTimestamp());
        transport.write(serializedMessage);

        session.updateLastSentTimestamp();

        updateEvent(event, sequenceNumber, serializedMessage);
    }

    private static void updateEvent(final OutboundEvent event, final int sequenceNumber, final byte[] message) {
        event.setSerializedFixMessage(message);
        event.setSequenceNumber(sequenceNumber);
    }
}
