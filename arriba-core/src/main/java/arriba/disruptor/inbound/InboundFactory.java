package arriba.disruptor.inbound;


import com.lmax.disruptor.EventFactory;

public final class InboundFactory implements EventFactory<InboundEvent> {

    @Override
    public InboundEvent newInstance() {
        return new InboundEvent();
    }
}
