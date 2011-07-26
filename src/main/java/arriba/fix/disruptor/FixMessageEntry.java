package arriba.fix.disruptor;

import java.util.List;

import arriba.fix.SerializedField;
import arriba.fix.messages.FixMessage;

import com.lmax.disruptor.AbstractEntry;

public final class FixMessageEntry extends AbstractEntry  {

    private List<SerializedField> serializedFields;
    private FixMessage fixMessage;

    public FixMessageEntry() {}

    public void setFixMessage(final FixMessage fixMessage) {
        this.fixMessage = fixMessage;
    }

    public FixMessage getFixMessage() {
        return this.fixMessage;
    }

    public void setSerializedFields(final List<SerializedField> serializedFields) {
        this.serializedFields = serializedFields;
    }

    public List<SerializedField> getSerializedFields() {
        return this.serializedFields;
    }
}
