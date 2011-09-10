package arriba.fix.disruptor;


import com.lmax.disruptor.EventFactory;

public final class FixMessageEntryFactory implements EventFactory<FixMessageEntry> {

    public FixMessageEntry create() {
        return new FixMessageEntry();
    }
}
