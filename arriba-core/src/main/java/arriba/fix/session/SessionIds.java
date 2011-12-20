package arriba.fix.session;

import arriba.fix.outbound.OutboundFixMessage;

public final class SessionIds {

    private SessionIds() {}

    public static SessionId newSessionId(final OutboundFixMessage message) {
        return new SessionId(message.getSenderCompId(), message.getTargetCompId());
    }
}
