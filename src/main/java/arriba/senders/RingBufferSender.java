package arriba.senders;

import arriba.common.MessageToRingBufferEntryAdapter;
import arriba.common.Sender;

import com.lmax.disruptor.AbstractEntry;
import com.lmax.disruptor.ProducerBarrier;

public final class RingBufferSender<M, E extends AbstractEntry> implements Sender<M> {

    private final ProducerBarrier<E> producerBarrier;
    private final MessageToRingBufferEntryAdapter<M, E> messageToRingBufferEntryAdapter;

    public RingBufferSender(final ProducerBarrier<E> producerBarrier,
            final MessageToRingBufferEntryAdapter<M, E> messageToRingBufferEntryAdapter) {
        this.producerBarrier = producerBarrier;
        this.messageToRingBufferEntryAdapter = messageToRingBufferEntryAdapter;
    }

    public void send(final M message) {
        final E nextEntry = this.producerBarrier.nextEntry();

        this.messageToRingBufferEntryAdapter.adapt(message, nextEntry);

        this.producerBarrier.commit(nextEntry);
    }
}
