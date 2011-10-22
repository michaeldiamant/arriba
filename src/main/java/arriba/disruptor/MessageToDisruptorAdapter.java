package arriba.disruptor;

public interface MessageToDisruptorAdapter<M, E> {

    void adapt(M message, E event);
}
