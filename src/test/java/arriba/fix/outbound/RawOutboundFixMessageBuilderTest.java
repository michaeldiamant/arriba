// FIXME

//package arriba.fix.outbound;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//
//import org.jboss.netty.buffer.ChannelBuffers;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
//import arriba.disruptor.inbound.InboundFixMessageEvent;
//import arriba.fix.Tags;
//import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
//import arriba.fix.inbound.InboundFixMessage;
//import arriba.fix.inbound.InboundFixMessageBuilder;
//import arriba.fix.inbound.InboundFixMessageFactory;
//import arriba.fix.inbound.RepeatingGroupBuilder;
//import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository;
//import arriba.utils.OutboundFixMessageFieldCapturer;
//
//import com.lmax.disruptor.EventHandler;
//
//@Ignore
//public class RawOutboundFixMessageBuilderTest {
//
//    private OutboundFixMessageFieldCapturer capturer;
//    private final int messageSequenceNumber = 0;
//    private final String sendingTime = DateSupplier.getUtcTimestamp();
//
//    @Before
//    public void before() {
//        this.capturer = new OutboundFixMessageFieldCapturer(new RawOutboundFixMessageBuilder());
//    }
//
//    @Test(expected=IllegalStateException.class)
//    public void testBuildingWithoutSettingTargetCompId() {
//        this.capturer.build();
//    }
//
//    @Test
//    public void testBuildingSingleSerializedFixMessage() throws Exception {
//        this.capturer.addField(Tags.BEGIN_STRING, "FIX.5.0");
//        this.capturer.addField(Tags.MESSAGE_TYPE, "D");
//        this.capturer.addField(Tags.SENDER_COMP_ID, "sender");
//        this.capturer.setTargetCompId("target");
//        this.capturer.addField(Tags.SYMBOL, "EURUSD");
//        this.capturer.addField(Tags.ORDER_TYPE, "1");
//        this.capturer.addField(Tags.ORDER_QUANTITY, "5");
//        this.capturer.addField(Tags.SIDE, "1");
//        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId");
//        this.capturer.addField(Tags.CHECKSUM, "1337");
//
//        final OutboundFixMessage outboundMessage = this.capturer.build();
//
//        final InboundFixMessage message = this.deserialize(outboundMessage.toBytes(this.messageSequenceNumber, this.sendingTime));
//
//        this.capturer.assertFieldsAreSet(message);
//        assertThat(message.getValue(Tags.TARGET_COMP_ID), is(outboundMessage.getTargetCompId()));
//    }
//
//    @Test
//    public void testBuildingTwoSerializedFixMessages() throws Exception {
//        this.capturer.addField(Tags.BEGIN_STRING, "FIX.5.0");
//        this.capturer.addField(Tags.MESSAGE_TYPE, "D");
//        this.capturer.addField(Tags.SENDER_COMP_ID, "sender");
//        this.capturer.setTargetCompId("target");
//        this.capturer.addField(Tags.SYMBOL, "EURUSD");
//        this.capturer.addField(Tags.ORDER_TYPE, "1");
//        this.capturer.addField(Tags.ORDER_QUANTITY, "5");
//        this.capturer.addField(Tags.SIDE, "1");
//        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId");
//        this.capturer.addField(Tags.CHECKSUM, "1337");
//
//        this.capturer.build();
//        this.capturer.clear();
//
//        this.capturer.addField(Tags.BEGIN_STRING, "FIX.4.4");
//        this.capturer.addField(Tags.MESSAGE_TYPE, "W");
//        this.capturer.addField(Tags.SENDER_COMP_ID, "sender1");
//        this.capturer.setTargetCompId("target1");
//        this.capturer.addField(Tags.ORDER_QUANTITY, "10");
//        this.capturer.addField(Tags.SIDE, "2");
//        this.capturer.addField(Tags.CLIENT_ORDER_ID, "clOrdId1");
//        this.capturer.addField(Tags.SYMBOL, "USDJPY");
//        this.capturer.addField(Tags.ORDER_TYPE, "2");
//        this.capturer.addField(Tags.PRICE, "1.32");
//        this.capturer.addField(Tags.CHECKSUM, "1337");
//
//        final OutboundFixMessage outboundMessage = this.capturer.build();
//
//        final InboundFixMessage message = this.deserialize(outboundMessage.toBytes(this.messageSequenceNumber, this.sendingTime));
//        this.capturer.assertFieldsAreSet(message);
//        assertThat(message.getValue(Tags.TARGET_COMP_ID), is(outboundMessage.getTargetCompId()));
//    }
//
//    private InboundFixMessage deserialize(final byte[] message) throws Exception {
//        final EventHandler<InboundFixMessageEvent> deserializer =
//                new DeserializingFixMessageEventHandler(
//                        new InboundFixMessageBuilder(
//                                new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository()),
//                                new InboundFixMessageFactory()
//                                ),
//                                new RepeatingGroupBuilder(null) // FIXME
//                        );
//
//
//        final InboundFixMessageEvent event = new InboundFixMessageEvent();
//        event.setSerializedFixMessage(ChannelBuffers.copiedBuffer(message));
//        deserializer.onEvent(event, true);
//
//        return event.getFixMessage();
//    }
//}
