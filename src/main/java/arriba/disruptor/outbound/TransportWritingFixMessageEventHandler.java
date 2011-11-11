package arriba.disruptor.outbound;

import java.io.IOException;

import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

import com.lmax.disruptor.EventHandler;

public final class TransportWritingFixMessageEventHandler<T> implements EventHandler<OutboundFixMessageEvent> {

    private final TransportRepository<String, T> transportRepository;

    public TransportWritingFixMessageEventHandler(final TransportRepository<String, T> transportRepository) {
        this.transportRepository = transportRepository;
    }

    @Override
    public void onEvent(final OutboundFixMessageEvent entry, final boolean endOfBatch) throws Exception {
        final OutboundFixMessage fixMessage = entry.getFixMessage();

        final Transport<T> transport = this.transportRepository.find(fixMessage.getTargetCompId());
        if (null == transport) {
            throw new IOException("");
        }

        // TODO Find session associated with message to get correct sequence number.
        final int messageSequenceNumber = 0;

        transport.write(fixMessage.toBytes(messageSequenceNumber, DateSupplier.getUtcTimestamp()));
    }
}
