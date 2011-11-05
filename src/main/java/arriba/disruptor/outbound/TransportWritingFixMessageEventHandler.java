package arriba.disruptor.outbound;

import java.io.IOException;

import arriba.disruptor.FixMessageEvent;
import arriba.fix.inbound.InboundFixMessage;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

import com.lmax.disruptor.EventHandler;

public final class TransportWritingFixMessageEventHandler<T> implements EventHandler<FixMessageEvent> {

    private final TransportRepository<String, T> transportRepository;

    public TransportWritingFixMessageEventHandler(final TransportRepository<String, T> transportRepository) {
        this.transportRepository = transportRepository;
    }

    @Override
    public void onEvent(final FixMessageEvent entry, final boolean b) throws Exception {
        final InboundFixMessage inboundFixMessage = entry.getFixMessage();

        final Transport<T> transport = this.transportRepository.find(inboundFixMessage.getTargetCompId());
        if (null == transport) {
            throw new IOException("");
        }

        transport.write(inboundFixMessage.toByteArray());
    }

    public void onEndOfBatch() throws Exception {}

}
