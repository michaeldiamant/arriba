// FIXME

//package arriba.utils;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//import arriba.fix.Tags;
//import arriba.fix.inbound.InboundFixMessage;
//import arriba.fix.outbound.OutboundFixMessage;
//import arriba.fix.outbound.RawOutboundFixMessageBuilder;
//
//public final class OutboundFixMessageFieldCapturer extends FieldCapturer {
//
//    private final RawOutboundFixMessageBuilder builder;
//
//    public OutboundFixMessageFieldCapturer(final RawOutboundFixMessageBuilder builder) {
//        this.builder = builder;
//    }
//
//    public RawOutboundFixMessageBuilder addField(final int tag, final String value) {
//        this.capture(tag, value);
//
//        return this.builder.addField(tag, value);
//    }
//
//    public RawOutboundFixMessageBuilder setTargetCompId(final String targetCompId) {
//        this.capture(Tags.TARGET_COMP_ID, targetCompId);
//
//        return this.builder.setTargetCompId(targetCompId);
//    }
//
//    public OutboundFixMessage build() {
//        return this.builder.build();
//    }
//
//    public void assertFieldsAreSet(final InboundFixMessage message) {
//        for (final Field<String> field : this.fields) {
//            assertThat(message.getValue(field.getTag()), is(field.getValue()));
//        }
//    }
//}
