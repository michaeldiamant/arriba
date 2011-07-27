package arriba.fix.disruptor;

import com.lmax.disruptor.BatchHandler;

public class DeserializingFixMessageEntryBatchHandler implements BatchHandler<FixMessageEntry> {

    public void onAvailable(final FixMessageEntry entry) throws Exception {
        entry.getSerializedFixMessage();

        // TODO Deserialize fields into FixMessage.

        entry.setFixMessage(null);
    }

    public void onEndOfBatch() throws Exception {}
}
