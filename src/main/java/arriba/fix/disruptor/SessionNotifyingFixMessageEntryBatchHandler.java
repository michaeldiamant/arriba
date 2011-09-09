package arriba.fix.disruptor;

import arriba.fix.messages.FixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;
import com.lmax.disruptor.EventHandler;

public final class SessionNotifyingFixMessageEntryBatchHandler implements EventHandler<FixMessageEntry> {

    private final SessionResolver sessionResolver;

    public SessionNotifyingFixMessageEntryBatchHandler(final SessionResolver sessionResolver) {
        this.sessionResolver = sessionResolver;
    }

  @Override
  public void onEvent(FixMessageEntry entry, boolean b) throws Exception {
        final FixMessage fixMessage = entry.getFixMessage();

        final Session session = this.sessionResolver.resolve(fixMessage.getSessionId());

        session.onMessage(fixMessage);
    }

    public void onEndOfBatch() throws Exception {}

}
