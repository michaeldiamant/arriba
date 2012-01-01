package arriba.disruptor.inbound;

public final class DeserializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeserializationException(final String message) {
        super(message);
    }
}
