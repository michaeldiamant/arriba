package arriba.common;

public interface MessageToRingBufferEntryAdapter<M, E> {

    void adapt(M message, E entry);
}
