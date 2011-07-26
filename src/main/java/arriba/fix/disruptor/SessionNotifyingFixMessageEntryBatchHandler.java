package arriba.fix.disruptor;

import arriba.fix.messages.FixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.BatchHandler;

public final class SessionNotifyingFixMessageEntryBatchHandler implements BatchHandler<FixMessageEntry> {

    private final SessionResolver sessionResolver;

    public SessionNotifyingFixMessageEntryBatchHandler(final SessionResolver sessionResolver) {
        this.sessionResolver = sessionResolver;
    }


    public void onAvailable(final FixMessageEntry entry) throws Exception {
        final FixMessage fixMessage = entry.getFixMessage();

        final Session session = this.sessionResolver.resolve(fixMessage.getSessionId());

        session.onMessage(fixMessage);
    }

    public void onEndOfBatch() throws Exception {}
}
