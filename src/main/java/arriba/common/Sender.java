package arriba.common;

import java.io.IOException;

public interface Sender<M> {

    void send(M message) throws IOException;
}
