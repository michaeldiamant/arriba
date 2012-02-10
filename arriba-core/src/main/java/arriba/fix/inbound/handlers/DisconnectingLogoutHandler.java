package arriba.fix.inbound.handlers;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.messages.Logout;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.LogoutTracker;
import arriba.fix.session.SessionId;
import arriba.fix.session.disconnect.SessionDisconnector;

public class DisconnectingLogoutHandler implements Handler<Logout> {

    private final Sender<OutboundFixMessage> sender;
    private final RichOutboundFixMessageBuilder builder;
    private final SessionDisconnector disconnector;
    private final LogoutTracker tracker;

    public DisconnectingLogoutHandler(final Sender<OutboundFixMessage> sender,
            final RichOutboundFixMessageBuilder builder,
            final SessionDisconnector disconnector,
            final LogoutTracker tracker) {
        this.sender = sender;
        this.builder = builder;
        this.disconnector = disconnector;
        this.tracker = tracker;
    }

    @Override
    public void handle(final Logout message) {
        final SessionId sessionId = new SessionId(message.getSenderCompId(), message.getTargetCompId());
        final boolean markedLogout = this.tracker.markLogout(sessionId);
        if (markedLogout) {
            final OutboundFixMessage logout = this.builder
                    .addStandardHeader(MessageType.LOGOUT, message)
                    .build();
            this.sender.send(logout);
        }

        this.disconnector.disconnect(sessionId);
    }
}
