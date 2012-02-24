package arriba.disruptor;

import arriba.transport.TransportIdentity;
import arriba.transport.TransportSender;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorTransportSender<T, M, E> implements TransportSender<T, M> {

    private final RingBuffer<E> buffer;
    private final TransportMessageToDisruptorAdapter<T, M, E> adapter;

    public DisruptorTransportSender(final RingBuffer<E> buffer,
                                    final TransportMessageToDisruptorAdapter<T, M, E> adapter) {
        this.buffer = buffer;
        this.adapter = adapter;
    }

    @Override
    public void send(TransportIdentity<T> identity, M message) {
        final long sequence = this.buffer.next();
        final E event = this.buffer.get(sequence);
        this.adapter.adapt(identity, message, event);
        this.buffer.publish(sequence);
    }
}
