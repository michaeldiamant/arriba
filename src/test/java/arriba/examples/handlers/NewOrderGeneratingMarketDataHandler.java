package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.FixMessageBuilder;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.fields.BeginString;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.MarketDataSnapshotFullRefresh;

public final class NewOrderGeneratingMarketDataHandler implements Handler<MarketDataSnapshotFullRefresh> {

    private static final String LIMIT = "2";
    private static final String BID = "0";
    private static final String BUY = "1";
    private static final String SELL = "2";
    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final AtomicInteger messageCount;
    private final Sender<InboundFixMessage> sender;
    private final FixMessageBuilder fixMessageBuilder = new FixMessageBuilder(new ArrayFixChunkBuilder(),
            new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder());

    public NewOrderGeneratingMarketDataHandler(final Sender<InboundFixMessage> sender, final AtomicInteger messageCount) {
        this.sender = sender;
        this.messageCount = messageCount;
    }

    @Override
    public void handle(final MarketDataSnapshotFullRefresh message) {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        if (this.messageCount.getAndIncrement() % 2 == 0) {
            this.fixMessageBuilder.addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.get()));
            this.fixMessageBuilder.setMessageType("D");
            this.fixMessageBuilder.setBeginStringBytes(BeginString.FIXT11);
            this.fixMessageBuilder.addField(Tags.SENDER_COMP_ID, message.getTargetCompId());
            this.fixMessageBuilder.addField(Tags.TARGET_COMP_ID, message.getSenderCompId());
            this.fixMessageBuilder.addField(Tags.SENDING_TIME, sdf.format(new Date()));

            this.fixMessageBuilder.addField(Tags.CLIENT_ORDER_ID, String.valueOf(this.messageCount.get()));
            this.fixMessageBuilder.addField(Tags.SYMBOL, message.getSymbol());


            final FixChunk[] mdEntries = message.getGroup(Tags.NUMBER_MD_ENTRIES);
            this.fixMessageBuilder.addField(Tags.PRICE, mdEntries[0].getValue(Tags.MD_ENTRY_PRICE));
            this.fixMessageBuilder.addField(Tags.ORDER_TYPE, LIMIT);
            if (mdEntries[0].getValue(Tags.MD_ENTRY_TYPE).equals(BID)) {
                this.fixMessageBuilder.addField(Tags.SIDE, BUY);
            } else {
                this.fixMessageBuilder.addField(Tags.SIDE, SELL);
            }

            try {
                this.sender.send(this.fixMessageBuilder.build());
            } catch (final IOException e) {
                e.printStackTrace();
            }
            this.fixMessageBuilder.clear();
        }
    }

}
