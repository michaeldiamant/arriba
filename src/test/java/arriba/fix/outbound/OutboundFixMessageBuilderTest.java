package arriba.fix.outbound;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

import arriba.disruptor.DeserializingFixMessageEventHandler;
import arriba.disruptor.FixMessageEvent;
import arriba.fix.FixMessageBuilder;
import arriba.fix.Tags;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.messages.FixMessage;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.OutboundFixMessageBuilder;
import arriba.utils.FieldCapturer;

import com.lmax.disruptor.EventHandler;

public class OutboundFixMessageBuilderTest {

    private final FieldCapturer capturer = new FieldCapturer();
    private OutboundFixMessageBuilder builder;

    @Before
    public void before() {
        this.capturer.reset();
        this.builder = new OutboundFixMessageBuilder();
    }

    @Test(expected=IllegalStateException.class)
    public void testBuildingWithoutSettingTargetCompId() {
        this.builder.build();
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

        final OutboundFixMessage outboundMessage = this.builder.build();

        final FixMessage message = this.deserialize(outboundMessage.getMessage());

        this.capturer.assertFieldsAreSet(message);
        assertThat(message.getValue(Tags.TARGET_COMP_ID), is(outboundMessage.getTargetCompId()));
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
        this.capturer.reset();

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

        final OutboundFixMessage outboundMessage = this.builder.build();

        final FixMessage message = this.deserialize(outboundMessage.getMessage());
        this.capturer.assertFieldsAreSet(message);
        assertThat(message.getValue(Tags.TARGET_COMP_ID), is(outboundMessage.getTargetCompId()));
    }

    private OutboundFixMessageBuilder addField(final int tag, final String value) {
        this.capturer.capture(tag, value);

        if (Tags.TARGET_COMP_ID == tag) {
            return this.builder.setTargetCompId(value);
        } else {
            return this.builder.addField(tag, value);
        }
    }

    private FixMessage deserialize(final byte[] message) throws Exception {
        final EventHandler<FixMessageEvent> deserializer = new DeserializingFixMessageEventHandler(new FixMessageBuilder(
                new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder()));

        final FixMessageEvent event = new FixMessageEvent();
        event.setSerializedFixMessage(ChannelBuffers.copiedBuffer(message));
        deserializer.onEvent(event, true);

        return event.getFixMessage();
    }
}
