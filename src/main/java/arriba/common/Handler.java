package arriba.common;

public interface Handler<M> {

    void handle(M message);
}
