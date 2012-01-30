package arriba.fix.session.messagejournal;



public final class InMemoryMessageJournal implements MessageJournal {

    private static final int INITIAL_MESSAGE_LIMIT = 1000 * 1000;
    private static final double CAPACITY_INCREASE_FACTOR = 0.5;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private byte[][] messages = new byte[INITIAL_MESSAGE_LIMIT][];

    @Override
    public void write(final int sequenceNumber, final byte[] message) {
        if (isExceedingCapacity(sequenceNumber, this.messages)) {
            this.messages = grow(this.messages);
        }

        this.messages[sequenceNumber] = message;
    }

    private static boolean isExceedingCapacity(final int sequenceNumber, final byte[][] messages) {
        if (sequenceNumber == MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }
        return sequenceNumber == messages.length - 1;
    }

    private static byte[][] grow(final byte[][] messages) {
        final byte[][] newMessages = new byte[calculateSize(messages.length)][];
        System.arraycopy(messages, 0, newMessages, 0, messages.length);
        return newMessages;
    }

    private static int calculateSize(final int currentLength) {
        return currentLength + (int) (currentLength * CAPACITY_INCREASE_FACTOR);
    }

    @Override
    public byte[][] retrieve(final int startSequenceNumber, final int endSequenceNumber) {
        if (0 == endSequenceNumber) {
            return getSubsequence(this.messages, startSequenceNumber, this.messages.length - 1);
        }

        if (endSequenceNumber <= startSequenceNumber) {
            return null;
        }

        if (endSequenceNumber >= this.messages.length) {
            return getSubsequence(this.messages, startSequenceNumber, this.messages.length - 1);
        }

        return getSubsequence(this.messages, startSequenceNumber, endSequenceNumber);
    }

    private static byte[][] getSubsequence(final byte[][] messages, final int startIndex, final int endIndex) {
        final byte[][] subsequence = new byte[endIndex - startIndex][];
        System.arraycopy(messages, startIndex, subsequence, 0, subsequence.length);
        return subsequence;
    }
}
