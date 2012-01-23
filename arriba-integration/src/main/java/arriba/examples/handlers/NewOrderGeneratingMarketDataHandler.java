package arriba.examples.handlers;

import java.util.concurrent.atomic.AtomicLong;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.MarketDataSnapshotFullRefresh;
import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;

public final class NewOrderGeneratingMarketDataHandler implements Handler<MarketDataSnapshotFullRefresh> {

    private static final String LIMIT = "2";
    private static final String BID = "0";
    private static final String BUY = "1";
    private static final String SELL = "2";

    private final Sender<OutboundFixMessage> sender;
    private final RichOutboundFixMessageBuilder builder;
    private final AtomicLong clOrdIdGenerator = new AtomicLong();

    public NewOrderGeneratingMarketDataHandler(final Sender<OutboundFixMessage> sender,
            final RichOutboundFixMessageBuilder builder) {
        this.sender = sender;
        this.builder = builder;
    }

    @Override
    public void handle(final MarketDataSnapshotFullRefresh message) {
        if (Integer.parseInt(message.getHeaderValue(Tags.MESSAGE_SEQUENCE_NUMBER)) % 2 == 0) {
            this.builder
            .addStandardHeader(MessageType.NEW_ORDER_SINGLE, message)

            .addField(Tags.CLIENT_ORDER_ID, String.valueOf(this.clOrdIdGenerator.getAndIncrement()))
            .addField(Tags.SYMBOL, message.getSymbol());

            final FixChunk[] mdEntries = message.getGroup(Tags.NUMBER_MD_ENTRIES);
            final FixChunk firstEntry = mdEntries[0];
            this.builder
            .addField(Tags.TRANSACTION_TIME, DateSupplier.getUtcTimestamp())
            .addField(Tags.PRICE, firstEntry.getValue(Tags.MD_ENTRY_PRICE))
            .addField(Tags.ORDER_TYPE, LIMIT)

            .addField(Tags.ORDER_QUANTITY, firstEntry.getValue(Tags.MD_ENTRY_SIZE));

            if (firstEntry.getValue(Tags.MD_ENTRY_TYPE).equals(BID)) {
                this.builder.addField(Tags.SIDE, BUY);
            } else {
                this.builder.addField(Tags.SIDE, SELL);
            }

            this.sender.send(this.builder.build());
        }
    }
}
