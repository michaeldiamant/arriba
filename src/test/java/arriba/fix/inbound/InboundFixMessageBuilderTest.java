package arriba.fix.inbound;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilderSupplier;
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

    private final FixChunkBuilderSupplier supplier = new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository());

    private final InboundFixMessageBuilder builder =
            new InboundFixMessageBuilder(
                    this.supplier,
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

    @Test
    public void testBuildingWithRepeatingGroups() {
        final MessageType messageType = MessageType.MARKET_DATA_REQUEST;
        final String mdRequestId = "mdReqId";
        final String mdStreamId = "mdStreamId";
        final String mdEntrySize = "100";
        final String mdEntryType = "1";
        final String mdEntryPrice = "1.2332";
        final String eurUsd = "EURUSD";
        final String usdJpy = "USDJPY";
        final String audCad = "AUDCAD";

        final RepeatingGroupBuilder groupBuilder = new RepeatingGroupBuilder(this.supplier);

        final FixChunk[][] repeatingGroups = groupBuilder
                .setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES)
                .addField(Tags.MD_ENTRY_SIZE, mdEntrySize.getBytes())
                .addField(Tags.MD_ENTRY_TYPE, mdEntryType.getBytes())
                .addField(Tags.MD_ENTRY_PRICE, mdEntryPrice.getBytes())

                .setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS)
                .addField(Tags.SYMBOL, eurUsd.getBytes())
                .addField(Tags.SYMBOL, usdJpy.getBytes())
                .addField(Tags.SYMBOL, audCad.getBytes())

                .build();

        final InboundFixMessage message = this.builder
                .addField(Tags.BEGIN_STRING, BEGIN_STRING.getSerializedValue())
                .addField(Tags.MESSAGE_SEQUENCE_NUMBER, MESSAGE_SEQUENCE_NUMBER.getBytes())
                .addField(Tags.MESSAGE_TYPE, messageType.getSerializedValue())

                .addField(Tags.MD_REQUEST_ID, mdRequestId.getBytes())
                .addField(Tags.MD_STREAM_ID, mdStreamId.getBytes())


                .build(repeatingGroups, groupBuilder.getNumberOfRepeatingGroupTags());

        final FixChunk[] entries = message.getGroup(Tags.NUMBER_MD_ENTRIES);
        assertNotNull(entries);
        assertThat(entries.length, is(1));

        final FixChunk[] symbols = message.getGroup(Tags.NUMBER_RELATED_SYMBOLS);
        assertNotNull(symbols);
        assertThat(symbols.length, is(3));
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
