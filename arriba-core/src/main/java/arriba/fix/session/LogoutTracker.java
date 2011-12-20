package arriba.fix.session;


public interface LogoutTracker {

    boolean markLogout(final SessionId sessionId);

    boolean clearMark(final SessionId sessionId);

    boolean hasIssuedLogout(final SessionId sessionId);
}
