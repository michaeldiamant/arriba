package arriba.common;

public interface HandlerRepository<ID, M> {

    void registerHandler(ID identifier, Handler<M> handler);

    Handler<M> findHandler(ID identifier) throws NonexistentHandlerException;
}
