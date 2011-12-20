package arriba.fix.session.messagejournal;

import arriba.fix.session.SessionId;

public interface MessageJournal {

    void write(byte[] message, SessionId sessionId, int sequenceNumber);

    byte[][] retrieve(SessionId sessionId, int startSequenceNumber, int endSequenceNumber);
}
