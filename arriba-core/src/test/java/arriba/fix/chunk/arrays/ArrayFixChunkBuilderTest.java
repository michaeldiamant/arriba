package arriba.fix.chunk.arrays;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.tagindexresolvers.NewOrderSingleTagIndexResolver;
import arriba.fix.tagindexresolvers.TagIndexResolver;
import arriba.utils.FixChunkFieldCapturer;

public class ArrayFixChunkBuilderTest {

    private final TagIndexResolver newOrderSingleResolver = new NewOrderSingleTagIndexResolver();
    private FixChunkFieldCapturer capturer;

    @Before
    public void before() {
        this.capturer = new FixChunkFieldCapturer(new ArrayFixChunkBuilder(this.newOrderSingleResolver));
    }

    @Test
    public void testBuildingSingleFixChunk() {
        this.capturer.addField(Tags.SYMBOL, "EURUSD");
        this.capturer.addField(Tags.ORDER_TYPE, "1");
        this.capturer.addField(Tags.ORDER_QUANTITY, "5");
        this.capturer.addField(Tags.SIDE, "1");
        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId");

        final FixChunk fixChunk = this.capturer.build();

        this.capturer.assertFieldsAreSet(fixChunk);
    }

    @Test
    public void testBuildingTwoFixChunks() {
        this.capturer.addField(Tags.SYMBOL, "EURUSD");
        this.capturer.addField(Tags.ORDER_TYPE, "1");
        this.capturer.addField(Tags.ORDER_QUANTITY, "5");
        this.capturer.addField(Tags.SIDE, "1");
        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId");

        this.capturer.build();
        this.capturer.clear();

        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId1");
        this.capturer.addField(Tags.SYMBOL, "USDJPY");
        this.capturer.addField(Tags.ACCOUNT, "acct1");
        this.capturer.addField(Tags.PRICE, "1.737");
        this.capturer.addField(Tags.ORDER_QUANTITY, "17");
        this.capturer.addField(Tags.ORDER_TYPE, "1");
        this.capturer.addField(Tags.SIDE, "2");

        final FixChunk fixChunk = this.capturer.build();

        this.capturer.assertFieldsAreSet(fixChunk);
    }
}
