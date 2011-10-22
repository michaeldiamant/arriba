package arriba.fix.outbound;

public final class OutboundFixMessage {

    private final byte[] message;
    private final String targetCompId;

    public OutboundFixMessage(final byte[] message, final String targetCompId) {
        this.message = message;
        this.targetCompId = targetCompId;
    }

    public byte[] getMessage() {
        return this.message;
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }
}
