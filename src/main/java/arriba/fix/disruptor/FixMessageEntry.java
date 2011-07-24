package arriba.fix.disruptor;

import arriba.fix.messages.FixMessage;

import com.lmax.disruptor.AbstractEntry;

public final class FixMessageEntry extends AbstractEntry  {

    private FixMessage fixMessage;

    public FixMessageEntry() {}

    public void setFixMessage(final FixMessage fixMessage) {
        this.fixMessage = fixMessage;
    }

    public FixMessage getFixMessage() {
        return this.fixMessage;
    }
}
