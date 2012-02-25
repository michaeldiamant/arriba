package arriba.disruptor.outbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arriba.common.Handler;
import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionIds;
import arriba.fix.session.SessionResolver;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

public final class TransportWritingFixMessageHandler<T> implements Handler<OutboundEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportWritingFixMessageHandler.class);

    private final TransportRepository<SessionId, T> transportRepository;
    private final SessionResolver sessionResolver;

    public TransportWritingFixMessageHandler(final TransportRepository<SessionId, T> transportRepository, final SessionResolver sessionResolver) {
        this.transportRepository = transportRepository;
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void handle(final OutboundEvent event) {
        final OutboundFixMessage message = event.getFixMessage();
        final SessionId sessionId = SessionIds.newSessionId(message);
        final Transport<T> transport = this.transportRepository.find(sessionId);
        if (null == transport) {
            throw new IllegalArgumentException("Cannot find transport for target comp ID " + message.getTargetCompId() + ".");
        }

        // TODO Can SessionId be cached?
        final Session session = this.sessionResolver.resolve(sessionId);

        final byte[] serializedMessage;
        if (event.isResend()) {
            serializedMessage = event.getSerializedFixMessage();
        } else {
            final int sequenceNumber = session.getNextOutboundSequenceNumber();
            serializedMessage = message.toBytes(sequenceNumber, DateSupplier.getUtcTimestamp());
            updateEvent(event, sequenceNumber, serializedMessage);
        }

        transport.write(serializedMessage);
        LOGGER.debug("Outbound:  {}", new String(serializedMessage));

        session.updateLastSentTimestamp();
    }

    private static void updateEvent(final OutboundEvent event, final int sequenceNumber, final byte[] message) {
        event.setSerializedFixMessage(message);
        event.setSequenceNumber(sequenceNumber);
    }
}
