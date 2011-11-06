package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.MarketDataSnapshotFullRefresh;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.OutboundFixMessageBuilder;

public final class NewOrderGeneratingMarketDataHandler implements Handler<MarketDataSnapshotFullRefresh> {

    private static final String LIMIT = "2";
    private static final String BID = "0";
    private static final String BUY = "1";
    private static final String SELL = "2";
    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final AtomicInteger messageCount;
    private final Sender<OutboundFixMessage> sender;
    private final OutboundFixMessageBuilder builder = new OutboundFixMessageBuilder();

    public NewOrderGeneratingMarketDataHandler(final Sender<OutboundFixMessage> sender, final AtomicInteger messageCount) {
        this.sender = sender;
        this.messageCount = messageCount;
    }

    @Override
    public void handle(final MarketDataSnapshotFullRefresh message) {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        if (this.messageCount.getAndIncrement() % 2 == 0) {
            this.builder
            .addField(Tags.BEGIN_STRING, new String(BeginString.FIXT11))
            .addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.get()))
            .addField(Tags.MESSAGE_TYPE, MessageType.NEW_ORDER_SINGLE.getValue())
            .addField(Tags.SENDER_COMP_ID, message.getTargetCompId())
            .addField(Tags.TARGET_COMP_ID, message.getSenderCompId())
            .addField(Tags.SENDING_TIME, sdf.format(new Date()))

            .addField(Tags.CLIENT_ORDER_ID, String.valueOf(this.messageCount.get()))
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
