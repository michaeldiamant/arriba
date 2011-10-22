package arriba.senders;

import java.io.IOException;

import arriba.common.Sender;
import arriba.disruptor.MessageToDisruptorAdapter;

import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

public final class RingBufferSender<M, E extends AbstractEvent> implements Sender<M> {

    private RingBuffer<E> outboundRingBuffer;
    private final MessageToDisruptorAdapter<M, E> messageToDisruptorAdapter;

    public RingBufferSender(final RingBuffer<E> producerBarrier,
            final MessageToDisruptorAdapter<M, E> messageToRingBufferEntryAdapter) {
        this.outboundRingBuffer = producerBarrier;
        this.messageToDisruptorAdapter = messageToRingBufferEntryAdapter;
    }

    public void setOutboundRingBuffer(final RingBuffer<E> outboundRingBuffer) {
        this.outboundRingBuffer = outboundRingBuffer;
    }

    public void send(final M message) throws IOException {
        final E nextEntry = this.outboundRingBuffer.nextEvent();

        this.messageToDisruptorAdapter.adapt(message, nextEntry);

        this.outboundRingBuffer.publish(nextEntry);
    }
}
