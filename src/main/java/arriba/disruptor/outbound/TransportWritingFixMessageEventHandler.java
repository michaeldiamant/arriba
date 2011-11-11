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
        final OutboundFixMessage fixMessage = entry.getFixMessage();

        final Transport<T> transport = this.transportRepository.find(fixMessage.getTargetCompId());
        if (null == transport) {
            throw new IOException("");
        }

        // TODO Can SessionId be cached?
        final Session session = this.sessionResolver.resolve(new SessionId("", fixMessage.getTargetCompId()));

        // TODO Find session associated with message to get correct sequence number.
        final int messageSequenceNumber = 0;

        transport.write(fixMessage.toBytes(messageSequenceNumber, DateSupplier.getUtcTimestamp()));
    }
}
