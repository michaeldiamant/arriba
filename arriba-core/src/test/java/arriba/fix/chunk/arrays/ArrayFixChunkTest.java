package arriba.fix.chunk.arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.tagindexresolvers.NewOrderSingleTagIndexResolver;

public class ArrayFixChunkTest {

    private final FixChunk chunk = this.buildInitializedFixChunk();

    private final String clientOrderId = "clOrdId";
    private final String side = "1";

    @Test
    public void testIsDefinedForWithUndefinedTag() {
        assertThat(this.chunk.isDefinedFor(Tags.BEGIN_STRING), is(false));
    }

    @Test
    public void testIsDefinedForWithDefinedTag() {
        assertThat(this.chunk.isDefinedFor(Tags.ORDER_TYPE), is(true));
    }

    @Test
    public void testGetSerializedValue() {
        final byte[] clientOrderIdBytes = this.chunk.getSerializedValue(Tags.CLIENT_ORDER_ID);

        assertThat(Arrays.equals(clientOrderIdBytes, this.clientOrderId.getBytes()), is(true));
    }

    @Test
    public void testGetValue() {
        assertThat(this.chunk.getValue(Tags.SIDE), is(this.side));
    }

    private FixChunk buildInitializedFixChunk() {
        return new ArrayFixChunkBuilder(new NewOrderSingleTagIndexResolver())
        .addField(Tags.SYMBOL, "EURUSD".getBytes())
        .addField(Tags.ORDER_TYPE, "1".getBytes())
        .addField(Tags.ORDER_QUANTITY, "5".getBytes())
        .addField(Tags.SIDE, this.side.getBytes())
        .addField(Tags.CLIENT_ORDER_ID, this.clientOrderId.getBytes())

        .build();
    }
}
