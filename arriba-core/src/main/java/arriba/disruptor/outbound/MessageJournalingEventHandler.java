package arriba.disruptor.outbound;

import arriba.fix.session.messagejournal.MessageJournal;

import com.lmax.disruptor.EventHandler;

public final class MessageJournalingEventHandler implements EventHandler<OutboundEvent> {

    private final MessageJournal journal;

    public MessageJournalingEventHandler(final MessageJournal journal) {
        this.journal = journal;
    }

    @Override
    public void onEvent(final OutboundEvent event, final boolean endOfBatch) throws Exception {
        this.journal.write(event.getSerializedFixMessage(), event.getSessionId(), event.getSequenceNumber());
    }
}
