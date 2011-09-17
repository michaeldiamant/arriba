package arriba.fix.disruptor;


import com.lmax.disruptor.EventFactory;

public final class FixMessageEventFactory implements EventFactory<FixMessageEvent> {

    public FixMessageEvent create() {
        return new FixMessageEvent();
    }
}
