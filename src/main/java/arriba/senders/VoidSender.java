package arriba.senders;

import java.io.IOException;

import arriba.common.Sender;

/**
 * A sender that only prints the message to be sent.
 * 
 * @param <M>
 */
public final class VoidSender<M> implements Sender<M> {

    public void send(final M message) throws IOException {
        System.out.println(message);
    }
}
