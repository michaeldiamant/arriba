package arriba.examples.handlers;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.MarketDataSnapshotFullRefresh;
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
            .addStandardHeader(MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, message)

            .addField(Tags.CLIENT_ORDER_ID, String.valueOf(this.clOrdIdGenerator.getAndIncrement()))
            .addField(Tags.SYMBOL, message.getSymbol());

            final FixChunk[] mdEntries = message.getGroup(Tags.NUMBER_MD_ENTRIES);
            this.builder
            .addField(Tags.PRICE, mdEntries[0].getValue(Tags.MD_ENTRY_PRICE))
            .addField(Tags.ORDER_TYPE, LIMIT);
            if (mdEntries[0].getValue(Tags.MD_ENTRY_TYPE).equals(BID)) {
                this.builder.addField(Tags.SIDE, BUY);
            } else {
                this.builder.addField(Tags.SIDE, SELL);
            }

            try {
                this.sender.send(this.builder.build());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
}
