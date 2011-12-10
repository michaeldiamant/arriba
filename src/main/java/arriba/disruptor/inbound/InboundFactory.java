package arriba.disruptor.inbound;


import com.lmax.disruptor.EventFactory;

public final class InboundFactory implements EventFactory<InboundEvent> {

    public InboundEvent create() {
        return new InboundEvent();
    }
}
