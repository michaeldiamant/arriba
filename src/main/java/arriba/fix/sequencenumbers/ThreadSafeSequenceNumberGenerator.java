package arriba.fix.sequencenumbers;

import java.util.concurrent.atomic.AtomicLong;

public final class ThreadSafeSequenceNumberGenerator implements SequenceNumberGenerator {

    private final AtomicLong generator = new AtomicLong();

    @Override
    public long get() {
        return this.generator.getAndIncrement();
    }

    @Override
    public void reset() {
        this.generator.set(0);
    }
}
