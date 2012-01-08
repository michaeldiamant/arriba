package arriba.disruptor;

import arriba.common.Sender;

import com.lmax.disruptor.RingBuffer;

public final class DisruptorSender<M, E> implements Sender<M> {

    private final RingBuffer<E> disruptor;
    private final MessageToDisruptorAdapter<M, E> adapter;

    public DisruptorSender(final RingBuffer<E> disruptor,
            final MessageToDisruptorAdapter<M, E> adapter) {
        this.disruptor = disruptor;
        this.adapter = adapter;
    }

    public void send(final M message) {
        final long sequence = this.disruptor.next();
        final E event = this.disruptor.get(sequence);
        this.adapter.adapt(message, event);
        this.disruptor.publish(sequence);
    }
}
