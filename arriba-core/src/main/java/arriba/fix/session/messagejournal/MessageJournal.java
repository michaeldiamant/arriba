package arriba.fix.session.messagejournal;


public interface MessageJournal {

    void write(int sequenceNumber, byte[] message);

    byte[][] retrieve(int startSequenceNumber, int endSequenceNumber);
}
