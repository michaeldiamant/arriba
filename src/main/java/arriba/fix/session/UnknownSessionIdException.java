package arriba.fix.session;

public final class UnknownSessionIdException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownSessionIdException(final String message) {
        super(message);
    }
}
