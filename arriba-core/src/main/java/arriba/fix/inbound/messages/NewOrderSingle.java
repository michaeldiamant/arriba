package arriba.fix.inbound.messages;

import arriba.fix.chunk.FixChunk;

public final class NewOrderSingle extends InboundFixMessage {

    protected NewOrderSingle(final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getSymbol() {
        return this.getValue(55);
    }

    public String getOrderType() {
        return this.getValue(40);
    }

    public String getOrderQuantity() {
        return this.getValue(38);
    }

    @Override
    public String toString() {
        return "NewOrderSingle -> 55=" + this.getSymbol() + " 40=" + this.getOrderType() +
                " 38=" + this.getOrderQuantity();
    }
}
