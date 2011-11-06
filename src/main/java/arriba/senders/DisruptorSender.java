package arriba.senders;

import java.io.IOException;

import arriba.common.Sender;
import arriba.disruptor.MessageToDisruptorAdapter;

import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorSender<M, E extends AbstractEvent> implements Sender<M> {

    private RingBuffer<E> disruptor;
    private final MessageToDisruptorAdapter<M, E> messageToDisruptorAdapter;

    public DisruptorSender(final RingBuffer<E> disruptor,
            final MessageToDisruptorAdapter<M, E> messageToDisruptorEntryAdapter) {
        this.disruptor = disruptor;
        this.messageToDisruptorAdapter = messageToDisruptorEntryAdapter;
    }

    public void setDisruptor(final RingBuffer<E> disruptor) {
        this.disruptor = disruptor;
    }

    public void send(final M message) throws IOException {
        final E nextEntry = this.disruptor.nextEvent();

        this.messageToDisruptorAdapter.adapt(message, nextEntry);

        this.disruptor.publish(nextEntry);
    }
}
