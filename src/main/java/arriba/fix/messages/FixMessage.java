package arriba.fix.messages;

import arriba.fix.FixFieldCollection;
import arriba.fix.session.SessionId;

public abstract class FixMessage {

    private final FixFieldCollection fixFieldCollection;

    protected FixMessage(final FixFieldCollection fixFieldCollection) {
        this.fixFieldCollection = fixFieldCollection;
    }

    public SessionId getSessionId() {
        return null;
    }

    public String getMessageType() {
        return this.getValue(35);
    }

    public String getSendingTime() {
        return this.getValue(52);
    }

    public String getSenderCompId() {
        return this.getValue(49);
    }

    public String getValue(final int tag) {
        return this.fixFieldCollection.getValue(tag);
    }
}
