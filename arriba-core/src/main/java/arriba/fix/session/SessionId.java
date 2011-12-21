package arriba.fix.session;

public final class SessionId {

    private final String senderCompId;
    private final String targetCompId;

    public SessionId(final String senderCompId, final String targetCompId) {
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
    }

    public String getSenderCompId() {
        return this.senderCompId;
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.senderCompId.hashCode();
        result = prime * result + this.targetCompId.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SessionId other = (SessionId) obj;
        return this.senderCompId.equals(other.senderCompId) && this.targetCompId.equals(other.targetCompId);
    }

    @Override
    public String toString() {
        return "SessionId [senderCompId=" + this.senderCompId + ", targetCompId=" + this.targetCompId + "]";
    }
}