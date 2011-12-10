package arriba.fix.session.disconnect;

import arriba.fix.session.SessionId;

public interface SessionDisconnectListener {

    void onDisconnect(SessionId sessionId);
}
