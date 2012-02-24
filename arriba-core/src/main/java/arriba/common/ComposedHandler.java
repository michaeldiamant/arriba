package arriba.common;

public final class ComposedHandler<M> implements Handler<M> {

    private final Handler<M>[] handlers;

    public ComposedHandler(final Handler<M>... handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(M message) {
        for (int handlerIndex = 0; handlerIndex < this.handlers.length; handlerIndex++) {
            handlers[handlerIndex].handle(message);
        }
    }
}
