package arriba.senders;

import java.io.IOException;

import arriba.common.Sender;
import arriba.fix.inbound.FixMessage;

public final class FixTimestampSender implements Sender<FixMessage> {

    public void send(final FixMessage message) throws IOException {
        final long sendingTime = Long.parseLong((message).getSendingTime());

        final long duration = System.currentTimeMillis() - sendingTime;
        System.out.println(duration);
    }

}
