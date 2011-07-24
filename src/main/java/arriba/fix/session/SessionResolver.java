package arriba.fix.session;

public interface SessionResolver {

    Session resolve(SessionId sessionId) throws UnknownSessionIdException;
}
