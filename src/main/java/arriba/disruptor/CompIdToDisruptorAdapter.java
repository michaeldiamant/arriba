package arriba.disruptor;

public interface CompIdToDisruptorAdapter<E> {

    void adapt(String compId, E event);
}
