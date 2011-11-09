package arriba.fix.inbound;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository;

public class InboundFixMessageBuilderTest {

    private static final BeginString BEGIN_STRING = BeginString.FIXT11;
    private static final String MESSAGE_SEQUENCE_NUMBER = "34";
    private static final MessageType MESSAGE_TYPE = MessageType.NEW_ORDER_SINGLE;
    private static final String ACCOUNT = "1";
    private static final String CLIENT_ORDER_ID = "clOrdId";
    private static final String ORDER_TYPE = "1";
    private static final String SIDE = "2";
    private static final String SYMBOL = "EURUSD";
    private static final String ORDER_QUANTITY = "100";

    private final InboundFixMessageBuilder builder =
            new InboundFixMessageBuilder(
                    new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository()),
                    new InboundFixMessageFactory()
                    );

    @Before
    public void before() {
        this.builder.clear();
    }

    @Test
    public void testTypeOfBuiltMessage() {
        final InboundFixMessage message = this.buildNewOrderSingle();

        assertEquals(NewOrderSingle.class, message.getClass());
    }

    @Test
    public void testGettingHeaderFields() {
        final InboundFixMessage message = this.buildNewOrderSingle();

        assertThat(message.getHeaderValue(Tags.BEGIN_STRING), is(BEGIN_STRING.getValue()));
        assertThat(message.getHeaderValue(Tags.MESSAGE_SEQUENCE_NUMBER), is(MESSAGE_SEQUENCE_NUMBER));
        assertThat(message.getHeaderValue(Tags.MESSAGE_TYPE), is(MESSAGE_TYPE.getValue()));
    }

    @Test
    public void testGettingBodyFields() {
        final InboundFixMessage message = this.buildNewOrderSingle();

        assertThat(message.getBodyValue(Tags.ACCOUNT), is(ACCOUNT));
        assertThat(message.getBodyValue(Tags.CLIENT_ORDER_ID), is(CLIENT_ORDER_ID));
        assertThat(message.getBodyValue(Tags.ORDER_TYPE), is(ORDER_TYPE));
        assertThat(message.getBodyValue(Tags.SIDE), is(SIDE));
        assertThat(message.getBodyValue(Tags.SYMBOL), is(SYMBOL));
        assertThat(message.getBodyValue(Tags.ORDER_QUANTITY), is(ORDER_QUANTITY));
    }

    @Test
    public void testGettingTrailerFields() {
        final InboundFixMessage message = this.buildNewOrderSingle();

        assertNotNull(message.getTrailerValue(Tags.CHECKSUM));
    }

    private InboundFixMessage buildNewOrderSingle() {
        return this.builder
                .addField(Tags.BEGIN_STRING, BEGIN_STRING.getSerializedValue())
                .addField(Tags.MESSAGE_SEQUENCE_NUMBER, MESSAGE_SEQUENCE_NUMBER.getBytes())
                .addField(Tags.MESSAGE_TYPE, MESSAGE_TYPE.getSerializedValue())

                .addField(Tags.ACCOUNT, ACCOUNT.getBytes())
                .addField(Tags.CLIENT_ORDER_ID, CLIENT_ORDER_ID.getBytes())
                .addField(Tags.ORDER_TYPE, ORDER_TYPE.getBytes())
                .addField(Tags.SIDE, SIDE.getBytes())
                .addField(Tags.SYMBOL, SYMBOL.getBytes())
                .addField(Tags.ORDER_QUANTITY, ORDER_QUANTITY.getBytes())

                .build();
    }
}
