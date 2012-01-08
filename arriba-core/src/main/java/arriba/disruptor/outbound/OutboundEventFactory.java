package arriba.disruptor.outbound;

import com.lmax.disruptor.EventFactory;

public final class OutboundEventFactory implements EventFactory<OutboundEvent> {

    @Override
    public OutboundEvent newInstance() {
        return new OutboundEvent();
    }
}
