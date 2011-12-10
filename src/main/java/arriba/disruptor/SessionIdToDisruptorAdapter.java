package arriba.disruptor;

import arriba.fix.session.SessionId;

public interface SessionIdToDisruptorAdapter<E> {

    void adapt(SessionId sessionId, E event);
}
