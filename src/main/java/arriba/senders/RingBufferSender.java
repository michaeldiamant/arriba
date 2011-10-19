package arriba.senders;

import java.io.IOException;

import arriba.common.MessageToRingBufferEntryAdapter;
import arriba.common.Sender;

import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

public final class RingBufferSender<M, E extends AbstractEvent> implements Sender<M> {

    private RingBuffer<E> outboundRingBuffer;
    private final MessageToRingBufferEntryAdapter<M, E> messageToRingBufferEntryAdapter;

    public RingBufferSender(final RingBuffer<E> producerBarrier,
            final MessageToRingBufferEntryAdapter<M, E> messageToRingBufferEntryAdapter) {
        this.outboundRingBuffer = producerBarrier;
        this.messageToRingBufferEntryAdapter = messageToRingBufferEntryAdapter;
    }

    public void setOutboundRingBuffer(final RingBuffer<E> outboundRingBuffer) {
        this.outboundRingBuffer = outboundRingBuffer;
    }

    public void send(final M message) throws IOException {
        final E nextEntry = this.outboundRingBuffer.nextEvent();

        this.messageToRingBufferEntryAdapter.adapt(message, nextEntry);

        this.outboundRingBuffer.publish(nextEntry);
    }
}
