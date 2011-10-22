package arriba.fix.chunk.arrays;

import arriba.fix.Tags;

public class NewOrderSingleTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new NewOrderSingleTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return new int[] {
                Tags.CLIENT_ORDER_ID,
                Tags.SYMBOL,
                Tags.SIDE,
                Tags.TRANSACTION_TIME,
                Tags.ORDER_QUANTITY,
                Tags.ORDER_TYPE
        };
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[] {
                Tags.ACCOUNT,
                Tags.PRICE
        };
    }
}
