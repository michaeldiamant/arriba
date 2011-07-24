package arriba.fix.disruptor;


import com.lmax.disruptor.EntryFactory;

public final class FixMessageEntryFactory implements EntryFactory<FixMessageEntry> {

    public FixMessageEntry create() {
        return new FixMessageEntry();
    }
}
