package arriba.senders;

import arriba.common.Sender;
import arriba.fix.messages.FixMessage;

public final class FixTimestampSender implements Sender<FixMessage> {

    public void send(final FixMessage message) {
        final long sendingTime = Long.parseLong((message).getSendingTime());

        final long duration = System.currentTimeMillis() - sendingTime;
        System.out.println(duration);
    }

}
