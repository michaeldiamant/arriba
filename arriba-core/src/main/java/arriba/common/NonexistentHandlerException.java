package arriba.common;

public final class NonexistentHandlerException extends Exception {

    private static final long serialVersionUID = 1L;

    public NonexistentHandlerException(final String message) {
        super(message);
    }
}
