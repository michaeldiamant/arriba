package arriba.fix.chunk.arrays;

import arriba.fix.Tags;

public class NewOrderSingleTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getTagIndexResolver() {
        return new NewOrderSingleTagIndexResolver();
    }

    @Override
    public int[] getAllTags() {
        return new int[] {
                Tags.CLIENT_ORDER_ID,
                Tags.SYMBOL,
                Tags.SIDE,
                Tags.TRANSACTION_TIME,
                Tags.ORDER_QUANTITY,
                Tags.ORDER_TYPE,
                Tags.ACCOUNT,
                Tags.PRICE
        };
    }
}
