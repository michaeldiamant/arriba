package arriba.disruptor;

import arriba.common.Sender;

import com.lmax.disruptor.RingBuffer;

public final class DisruptorSender<M, E> implements Sender<M> {

    private final RingBuffer<E> buffer;
    private final MessageToDisruptorAdapter<M, E> adapter;

    public DisruptorSender(final RingBuffer<E> buffer,
                           final MessageToDisruptorAdapter<M, E> adapter) {
        this.buffer = buffer;
        this.adapter = adapter;
    }

    public void send(final M message) {
        final long sequence = this.buffer.next();
        final E event = this.buffer.get(sequence);
        this.adapter.adapt(message, event);
        this.buffer.publish(sequence);
    }
}
