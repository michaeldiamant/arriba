package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class NewOrderSingleTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.CLIENT_ORDER_ID,
                Tags.SYMBOL,
                Tags.SIDE,
                Tags.TRANSACTION_TIME,
                Tags.ORDER_QUANTITY,
                Tags.ORDER_TYPE,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[] {
                Tags.ACCOUNT,
                Tags.PRICE
        };
    }
}
