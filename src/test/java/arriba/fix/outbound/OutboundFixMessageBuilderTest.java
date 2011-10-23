package arriba.fix.outbound;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

import arriba.disruptor.FixMessageEvent;
import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
import arriba.fix.Tags;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.tagindexresolvers.NewOrderSingleTagIndexResolver;
import arriba.fix.tagindexresolvers.StandardHeaderTagIndexResolver;
import arriba.fix.tagindexresolvers.StandardTrailerTagIndexResolver;
import arriba.utils.OutboundFixMessageFieldCapturer;

import com.lmax.disruptor.EventHandler;

public class OutboundFixMessageBuilderTest {

    private OutboundFixMessageFieldCapturer capturer;

    @Before
    public void before() {
        this.capturer = new OutboundFixMessageFieldCapturer(new OutboundFixMessageBuilder());
    }

    @Test(expected=IllegalStateException.class)
    public void testBuildingWithoutSettingTargetCompId() {
        this.capturer.build();
    }

    @Test
    public void testBuildingSingleSerializedFixMessage() throws Exception {
        this.capturer.addField(Tags.BEGIN_STRING, "FIX.5.0");
        this.capturer.addField(Tags.MESSAGE_TYPE, "D");
        this.capturer.addField(Tags.SENDER_COMP_ID, "sender");
        this.capturer.setTargetCompId("target");
        this.capturer.addField(Tags.SYMBOL, "EURUSD");
        this.capturer.addField(Tags.ORDER_TYPE, "1");
        this.capturer.addField(Tags.ORDER_QUANTITY, "5");
        this.capturer.addField(Tags.SIDE, "1");
        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId");
        this.capturer.addField(Tags.CHECKSUM, "1337");

        final OutboundFixMessage outboundMessage = this.capturer.build();

        final InboundFixMessage message = this.deserialize(outboundMessage.getMessage());

        this.capturer.assertFieldsAreSet(message);
        assertThat(message.getValue(Tags.TARGET_COMP_ID), is(outboundMessage.getTargetCompId()));
    }

    @Test
    public void testBuildingTwoSerializedFixMessages() throws Exception {
        this.capturer.addField(Tags.BEGIN_STRING, "FIX.5.0");
        this.capturer.addField(Tags.MESSAGE_TYPE, "D");
        this.capturer.addField(Tags.SENDER_COMP_ID, "sender");
        this.capturer.setTargetCompId("target");
        this.capturer.addField(Tags.SYMBOL, "EURUSD");
        this.capturer.addField(Tags.ORDER_TYPE, "1");
        this.capturer.addField(Tags.ORDER_QUANTITY, "5");
        this.capturer.addField(Tags.SIDE, "1");
        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId");
        this.capturer.addField(Tags.CHECKSUM, "1337");

        this.capturer.build();
        this.capturer.clear();

        this.capturer.addField(Tags.BEGIN_STRING, "FIX.4.4");
        this.capturer.addField(Tags.MESSAGE_TYPE, "W");
        this.capturer.addField(Tags.SENDER_COMP_ID, "sender1");
        this.capturer.setTargetCompId("target1");
        this.capturer.addField(Tags.ORDER_QUANTITY, "10");
        this.capturer.addField(Tags.SIDE, "2");
        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId1");
        this.capturer.addField(Tags.SYMBOL, "USDJPY");
        this.capturer.addField(Tags.ORDER_TYPE, "2");
        this.capturer.addField(Tags.PRICE, "1.32");
        this.capturer.addField(Tags.CHECKSUM, "1337");

        final OutboundFixMessage outboundMessage = this.capturer.build();

        final InboundFixMessage message = this.deserialize(outboundMessage.getMessage());
        this.capturer.assertFieldsAreSet(message);
        assertThat(message.getValue(Tags.TARGET_COMP_ID), is(outboundMessage.getTargetCompId()));
    }

    private InboundFixMessage deserialize(final byte[] message) throws Exception {
        final EventHandler<FixMessageEvent> deserializer =
                new DeserializingFixMessageEventHandler(
                        new InboundFixMessageBuilder(
                                new ArrayFixChunkBuilder(new StandardHeaderTagIndexResolver()),
                                new ArrayFixChunkBuilder(new NewOrderSingleTagIndexResolver()),
                                new ArrayFixChunkBuilder(new StandardTrailerTagIndexResolver())
                                )
                        );

        final FixMessageEvent event = new FixMessageEvent();
        event.setSerializedFixMessage(ChannelBuffers.copiedBuffer(message));
        deserializer.onEvent(event, true);

        return event.getFixMessage();
    }
}
