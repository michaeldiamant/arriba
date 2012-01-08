package arriba.disruptor;

import arriba.fix.session.SessionId;
import arriba.fix.session.disconnect.SessionDisconnector;

import com.lmax.disruptor.RingBuffer;

public final class DisruptorSessionDisconnector<E> implements SessionDisconnector {
    private final RingBuffer<E> disruptor;
    private final SessionIdToDisruptorAdapter<E> adapter;

    public DisruptorSessionDisconnector(final RingBuffer<E> disruptor, final SessionIdToDisruptorAdapter<E> adapter) {
        this.disruptor = disruptor;
        this.adapter = adapter;
    }

    @Override
    public void disconnect(final SessionId sessionId) {
        final long sequence = this.disruptor.next();
        final E event = this.disruptor.get(sequence);
        this.adapter.adapt(sessionId, event);
        this.disruptor.publish(sequence);
    }
}
