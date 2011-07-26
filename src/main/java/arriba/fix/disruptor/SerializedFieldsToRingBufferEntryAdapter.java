package arriba.fix.disruptor;

import java.util.List;

import arriba.scala.common.MessageToRingBufferEntryAdapter;
import arriba.fix.SerializedField;

public final class SerializedFieldsToRingBufferEntryAdapter implements MessageToRingBufferEntryAdapter<List<SerializedField>, FixMessageEntry>{

    public void adapt(final List<SerializedField> serializedFields, final FixMessageEntry entry) {
        entry.setSerializedFields(serializedFields);
    }
}
