package arriba.fix.messages;

import arriba.fix.FixFieldCollection;

public final class FixMessageFactory {

    public static FixMessage create(final FixFieldCollection fixFieldCollection, final String messageType) {
        if ("D".equals(messageType)) {
            return new NewOrderSingle(fixFieldCollection);
        } else {
            throw new IllegalArgumentException("Message type " + messageType + " does not map to a known FIX message.");
        }

    }
}
