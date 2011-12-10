package arriba.disruptor.outbound;

import com.lmax.disruptor.EventFactory;

public final class OutboundEventFactory implements EventFactory<OutboundEvent> {

    public OutboundEvent create() {
        return new OutboundEvent();
    }
}
