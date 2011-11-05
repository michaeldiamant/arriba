package arriba.disruptor.inbound;


import com.lmax.disruptor.EventFactory;

public final class InboundFixMessageEventFactory implements EventFactory<InboundFixMessageEvent> {

    public InboundFixMessageEvent create() {
        return new InboundFixMessageEvent();
    }
}
