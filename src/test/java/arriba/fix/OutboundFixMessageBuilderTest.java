package arriba.fix;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

import arriba.fix.chunk.arrays.ArrayFixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.disruptor.DeserializingFixMessageEventHandler;
import arriba.fix.disruptor.FixMessageEvent;
import arriba.fix.messages.FixMessage;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;

public class OutboundFixMessageBuilderTest {

    private final List<Field<String>> fields = Lists.newArrayList();
    private OutboundFixMessageBuilder builder;

    @Before
    public void before() {
        this.fields.clear();
        this.builder = new OutboundFixMessageBuilder();
    }

    @Test
    public void testBuildingSingleSerializedFixMessage() throws Exception {
        this.addField(Tags.BEGIN_STRING, "FIX.5.0");
        this.addField(Tags.MESSAGE_TYPE, "D");
        this.addField(Tags.SENDER_COMP_ID, "sender");
        this.addField(Tags.TARGET_COMP_ID, "target");
        this.addField(Tags.SYMBOL, "EURUSD");
        this.addField(Tags.ORDER_TYPE, "1");
        this.addField(Tags.ORDER_QUANTITY, "5");
        this.addField(Tags.SIDE, "1");
        this.addField(Tags.CLIENT_ORDER_ID, "clOrdId");
        this.addField(Tags.CHECKSUM, "1337");

        final byte[] messageBytes = this.builder.build();
        final FixMessage message = this.deserialize(messageBytes);

        this.assertFixFieldsAreSet(message);
    }

    @Test
    public void testBuildingTwoSerializedFixMessages() throws Exception {
        this.addField(Tags.BEGIN_STRING, "FIX.5.0");
        this.addField(Tags.MESSAGE_TYPE, "D");
        this.addField(Tags.SENDER_COMP_ID, "sender");
        this.addField(Tags.TARGET_COMP_ID, "target");
        this.addField(Tags.SYMBOL, "EURUSD");
        this.addField(Tags.ORDER_TYPE, "1");
        this.addField(Tags.ORDER_QUANTITY, "5");
        this.addField(Tags.SIDE, "1");
        this.addField(Tags.CLIENT_ORDER_ID, "clOrdId");
        this.addField(Tags.CHECKSUM, "1337");

        this.builder.build();
        this.fields.clear();

        this.addField(Tags.BEGIN_STRING, "FIX.4.4");
        this.addField(Tags.MESSAGE_TYPE, "W");
        this.addField(Tags.SENDER_COMP_ID, "sender1");
        this.addField(Tags.TARGET_COMP_ID, "target1");
        this.addField(Tags.MARKET_DEPTH, "1");
        this.addField(Tags.SENDING_TIME, "now");
        this.addField(Tags.TRANSACTION_TIME, "now");
        this.addField(Tags.MD_ENTRY_PRICE, "1.245");
        this.addField(Tags.MD_ENTRY_SIZE, "5");
        this.addField(Tags.MD_ENTRY_TYPE, "1");
        this.addField(Tags.MD_REQUEST_ID, "reqId1");
        this.addField(Tags.CHECKSUM, "1337");

        final byte[] messageBytes = this.builder.build();
        final FixMessage message = this.deserialize(messageBytes);

        this.assertFixFieldsAreSet(message);
    }

    private void assertFixFieldsAreSet(final FixMessage message) {
        for (final Field<String> field : this.fields) {
            assertThat(message.getValue(field.getTag()), is(field.getValue()));
        }
    }

    private OutboundFixMessageBuilder addField(final int tag, final String value) {
        this.fields.add(new Field<String>(tag, value));
        return this.builder.addField(tag, value);
    }

    private FixMessage deserialize(final byte[] message) throws Exception {
        final EventHandler<FixMessageEvent> deserializer = new DeserializingFixMessageEventHandler(new FixMessageBuilder<ArrayFixChunk>(
                new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder()));

        final FixMessageEvent event = new FixMessageEvent();
        event.setSerializedFixMessage(ChannelBuffers.copiedBuffer(message));
        deserializer.onEvent(event, true);

        return event.getFixMessage();
    }
}
