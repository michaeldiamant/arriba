package arriba.examples.handlers;

import arriba.common.Handler;
import arriba.fix.inbound.Heartbeat;

public final class NoOpHeartbeatHandler implements Handler<Heartbeat> {

    @Override
    public void handle(final Heartbeat message) {}
}
