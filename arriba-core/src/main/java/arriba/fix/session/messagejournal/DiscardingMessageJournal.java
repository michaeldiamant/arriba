package arriba.fix.session.messagejournal;

public final class DiscardingMessageJournal implements MessageJournal {

    @Override
    public void write(final int sequenceNumber, final byte[] message) {}

    @Override
    public byte[][] retrieve(final int startSequenceNumber, final int endSequenceNumber) {
        return null;
    }
}
