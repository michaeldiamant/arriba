package arriba.transport.channels;

public final class UnknownChannelIdException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownChannelIdException(final String message) {
        super(message);
    }
}
