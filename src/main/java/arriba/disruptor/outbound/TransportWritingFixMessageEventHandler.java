package arriba.disruptor.outbound;

import java.io.IOException;

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
    public void onEvent(final OutboundFixMessageEvent entry, final boolean b) throws Exception {
        final OutboundFixMessage fixMessage = entry.getFixMessage();

        final Transport<T> transport = this.transportRepository.find(fixMessage.getTargetCompId());
        if (null == transport) {
            throw new IOException("");
        }

        transport.write(fixMessage.getMessage());
    }

    public void onEndOfBatch() throws Exception {}

}
