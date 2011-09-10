package arriba.senders;

import arriba.common.MessageToRingBufferEntryAdapter;
import arriba.common.Sender;
import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

import java.io.IOException;

public final class RingBufferSender<M, E extends AbstractEvent> implements Sender<M> {

    private final RingBuffer<E> outboundRingBuffer;
    private final MessageToRingBufferEntryAdapter<M, E> messageToRingBufferEntryAdapter;

    public RingBufferSender(final RingBuffer<E> producerBarrier,
            final MessageToRingBufferEntryAdapter<M, E> messageToRingBufferEntryAdapter) {
        this.outboundRingBuffer = producerBarrier;
        this.messageToRingBufferEntryAdapter = messageToRingBufferEntryAdapter;
    }

    public void send(final M message) throws IOException {
        final E nextEntry = this.outboundRingBuffer.nextEvent();

        this.messageToRingBufferEntryAdapter.adapt(message, nextEntry);

        this.outboundRingBuffer.publish(nextEntry);
    }
}
