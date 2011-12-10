package arriba.fix.session.disconnect;

import arriba.fix.session.SessionId;

public interface SessionDisconnector {

    void disconnect(SessionId sessionId);
}
