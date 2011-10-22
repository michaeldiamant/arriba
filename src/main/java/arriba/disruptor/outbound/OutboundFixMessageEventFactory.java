package arriba.disruptor.outbound;

import com.lmax.disruptor.EventFactory;

public final class OutboundFixMessageEventFactory implements EventFactory<OutboundFixMessageEvent> {

    public OutboundFixMessageEvent create() {
        return new OutboundFixMessageEvent();
    }
}
