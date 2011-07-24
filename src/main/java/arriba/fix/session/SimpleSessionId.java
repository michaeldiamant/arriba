package arriba.fix.session;

public final class SimpleSessionId implements SessionId {

    private final String targetCompId;

    public SimpleSessionId(final String targetCompId) {
        this.targetCompId = targetCompId;
    }

    public String getBeginString() {
        throw new UnsupportedOperationException();
    }

    public String getSenderCompId() {
        throw new UnsupportedOperationException();
    }

    public String getSenderSubId() {
        throw new UnsupportedOperationException();
    }

    public String getSenderLocationId() {
        throw new UnsupportedOperationException();
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }

    public String getTargetSubId() {
        throw new UnsupportedOperationException();
    }

    public String getTargetLocationId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.targetCompId == null) ? 0 : this.targetCompId.hashCode());
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

        final SimpleSessionId other = (SimpleSessionId) obj;
        if (this.targetCompId == null) {
            if (other.targetCompId != null) {
                return false;
            }
        } else if (!this.targetCompId.equals(other.targetCompId)) {
            return false;
        }

        return true;
    }
}
