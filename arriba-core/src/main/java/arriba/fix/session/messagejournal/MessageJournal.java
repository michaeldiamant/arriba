package arriba.fix.session.messagejournal;


public interface MessageJournal {

    void write(byte[] message, int sequenceNumber);

    byte[][] retrieve(int startSequenceNumber, int endSequenceNumber);
}
