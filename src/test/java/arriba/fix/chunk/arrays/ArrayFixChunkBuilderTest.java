package arriba.fix.chunk.arrays;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.utils.FixChunkFieldCapturer;

public class ArrayFixChunkBuilderTest {

    private FixChunkFieldCapturer capturer;

    @Before
    public void before() {
        this.capturer = new FixChunkFieldCapturer(new ArrayFixChunkBuilder());
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

        this.capturer.addField(Tags.MARKET_DEPTH, "1");
        this.capturer.addField(Tags.SENDING_TIME, "now");
        this.capturer.addField(Tags.TRANSACTION_TIME, "now");
        this.capturer.addField(Tags.MD_ENTRY_PRICE, "1.245");
        this.capturer.addField(Tags.MD_ENTRY_SIZE, "5");
        this.capturer.addField(Tags.MD_ENTRY_TYPE, "1");
        this.capturer.addField(Tags.MD_REQUEST_ID, "reqId1");

        final FixChunk fixChunk = this.capturer.build();

        this.capturer.assertFieldsAreSet(fixChunk);
    }
}
