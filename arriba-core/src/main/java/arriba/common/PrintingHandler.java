package arriba.common;

public final class PrintingHandler<T> implements Handler<T> {

    public void handle(final T message) {
        System.out.println(this.getClass() + " received " + message);
    }
}
