package arriba.fix.inbound.handlers;

import arriba.common.Handler;
import arriba.fix.inbound.messages.Heartbeat;

public final class NoOpHeartbeatHandler implements Handler<Heartbeat> {

    @Override
    public void handle(final Heartbeat message) {}
}
