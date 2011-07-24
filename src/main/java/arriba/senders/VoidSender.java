package arriba.senders;

import arriba.common.Sender;

/**
 * A sender that only prints the message to be sent.
 * 
 * @param <M>
 */
public final class VoidSender<M> implements Sender<M> {

    public void send(final M message) {
        System.out.println(message);
    }
}
