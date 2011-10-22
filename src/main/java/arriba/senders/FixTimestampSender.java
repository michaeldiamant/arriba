package arriba.senders;

import java.io.IOException;

import arriba.common.Sender;
import arriba.fix.inbound.InboundFixMessage;

public final class FixTimestampSender implements Sender<InboundFixMessage> {

    public void send(final InboundFixMessage message) throws IOException {
        final long sendingTime = Long.parseLong((message).getSendingTime());

        final long duration = System.currentTimeMillis() - sendingTime;
        System.out.println(duration);
    }

}
