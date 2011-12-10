package arriba.disruptor;

import arriba.fix.session.SessionDisconnector;

import com.lmax.disruptor.AbstractEvent;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorSessionDisconnector<E extends AbstractEvent> implements SessionDisconnector {
    private final RingBuffer<E> disruptor;
    private final CompIdToDisruptorAdapter<E> adapter;

    public DisruptorSessionDisconnector(final RingBuffer<E> disruptor, final CompIdToDisruptorAdapter<E> adapter) {
        this.disruptor = disruptor;
        this.adapter = adapter;
    }

    @Override
    public void disconnect(final String targetCompId) {
        final E event = this.disruptor.nextEvent();
        this.adapter.adapt(targetCompId, event);
        this.disruptor.publish(event);
    }
}
