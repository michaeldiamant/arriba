package arriba.disruptor;

import arriba.common.Sender;

import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorSender<M, E extends AbstractEvent> implements Sender<M> {

    private final RingBuffer<E> disruptor;
    private final MessageToDisruptorAdapter<M, E> messageToDisruptorAdapter;

    public DisruptorSender(final RingBuffer<E> disruptor,
            final MessageToDisruptorAdapter<M, E> messageToDisruptorEntryAdapter) {
        this.disruptor = disruptor;
        this.messageToDisruptorAdapter = messageToDisruptorEntryAdapter;
    }

    public void send(final M message) {
        final E nextEntry = this.disruptor.nextEvent();

        this.messageToDisruptorAdapter.adapt(message, nextEntry);

        this.disruptor.publish(nextEntry);
    }
}
