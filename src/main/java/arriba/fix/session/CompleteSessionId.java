package arriba.fix.session;

public final class CompleteSessionId implements SessionId {

    private final String beginString;
    private final String senderCompId;
    private final String senderSubId;
    private final String senderLocationId;
    private final String targetCompId;
    private final String targetSubId;
    private final String targetLocationId;

    public CompleteSessionId(final String beginString, final String senderCompId, final String senderSubId, final String senderLocationId,
            final String targetCompId, final String targetSubId, final String targetLocationId) {
        this.beginString = beginString;
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.senderLocationId = senderLocationId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
        this.targetLocationId = targetLocationId;
    }

    public String getBeginString() {
        return this.beginString;
    }

    public String getSenderCompId() {
        return this.senderCompId;
    }

    public String getSenderSubId() {
        return this.senderSubId;
    }

    public String getSenderLocationId() {
        return this.senderLocationId;
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }

    public String getTargetSubId() {
        return this.targetSubId;
    }

    public String getTargetLocationId() {
        return this.targetLocationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.beginString == null) ? 0 : this.beginString.hashCode());
        result = prime * result + ((this.senderCompId == null) ? 0 : this.senderCompId.hashCode());
        result = prime * result + ((this.senderLocationId == null) ? 0 : this.senderLocationId.hashCode());
        result = prime * result + ((this.senderSubId == null) ? 0 : this.senderSubId.hashCode());
        result = prime * result + ((this.targetCompId == null) ? 0 : this.targetCompId.hashCode());
        result = prime * result + ((this.targetLocationId == null) ? 0 : this.targetLocationId.hashCode());
        result = prime * result + ((this.targetSubId == null) ? 0 : this.targetSubId.hashCode());
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
        final CompleteSessionId other = (CompleteSessionId) obj;
        if (this.beginString == null) {
            if (other.beginString != null) {
                return false;
            }
        } else if (!this.beginString.equals(other.beginString)) {
            return false;
        }
        if (this.senderCompId == null) {
            if (other.senderCompId != null) {
                return false;
            }
        } else if (!this.senderCompId.equals(other.senderCompId)) {
            return false;
        }
        if (this.senderLocationId == null) {
            if (other.senderLocationId != null) {
                return false;
            }
        } else if (!this.senderLocationId.equals(other.senderLocationId)) {
            return false;
        }
        if (this.senderSubId == null) {
            if (other.senderSubId != null) {
                return false;
            }
        } else if (!this.senderSubId.equals(other.senderSubId)) {
            return false;
        }
        if (this.targetCompId == null) {
            if (other.targetCompId != null) {
                return false;
            }
        } else if (!this.targetCompId.equals(other.targetCompId)) {
            return false;
        }
        if (this.targetLocationId == null) {
            if (other.targetLocationId != null) {
                return false;
            }
        } else if (!this.targetLocationId.equals(other.targetLocationId)) {
            return false;
        }
        if (this.targetSubId == null) {
            if (other.targetSubId != null) {
                return false;
            }
        } else if (!this.targetSubId.equals(other.targetSubId)) {
            return false;
        }
        return true;
    }
}
