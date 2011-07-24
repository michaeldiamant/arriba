package arriba.fix.disruptor;

import com.lmax.disruptor.BatchHandler;

public final class SendingFixMessageEntryBatchHandler implements BatchHandler<FixMessageEntry> {

    public void onAvailable(final FixMessageEntry entry) throws Exception {

    }

    public void onEndOfBatch() throws Exception {}
}
