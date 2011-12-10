package arriba.disruptor;

import arriba.fix.session.SessionDisconnector;
import arriba.fix.session.SessionId;

import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorSessionDisconnector<E extends AbstractEvent> implements SessionDisconnector {
    private final RingBuffer<E> disruptor;
    private final SessionIdToDisruptorAdapter<E> adapter;

    public DisruptorSessionDisconnector(final RingBuffer<E> disruptor, final SessionIdToDisruptorAdapter<E> adapter) {
        this.disruptor = disruptor;
        this.adapter = adapter;
    }

    @Override
    public void disconnect(final SessionId sessionId) {
        final E event = this.disruptor.nextEvent();
        this.adapter.adapt(sessionId, event);
        this.disruptor.publish(event);
    }
}
