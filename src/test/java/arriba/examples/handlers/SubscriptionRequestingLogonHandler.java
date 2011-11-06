package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.Logon;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.OutboundFixMessageBuilder;

public final class SubscriptionRequestingLogonHandler implements Handler<Logon> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";
    private static final String FULL_BOOK_MARKET_DEPTH = "0";
    private static final String SNAPSHOT_AND_UPDATES_REQUEST = "1";

    private final Set<String> symbolsToSubscribe;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final AtomicInteger messageCount;

    private final OutboundFixMessageBuilder builder = new OutboundFixMessageBuilder();

    public SubscriptionRequestingLogonHandler(final Set<String> symbolsToSubscribe,
            final Sender<OutboundFixMessage> fixMessageSender,
            final AtomicInteger messageCount) {
        this.symbolsToSubscribe = symbolsToSubscribe;
        this.fixMessageSender = fixMessageSender;
        this.messageCount = messageCount;
    }

    @Override
    public void handle(final Logon message) {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.builder
        .addField(Tags.BEGIN_STRING, new String(BeginString.FIXT11))
        .addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.get()))
        .addField(Tags.MESSAGE_TYPE, MessageType.MARKET_DATA_REQUEST.getValue())
        .addField(Tags.SENDER_COMP_ID, message.getTargetCompId())
        .addField(Tags.TARGET_COMP_ID, message.getSenderCompId())
        .addField(Tags.SENDING_TIME, sdf.format(new Date()))

        // TODO Consolidate the standard header tags boiler plate logic.

        .addField(Tags.MD_REQUEST_ID, String.valueOf(this.messageCount.get()))
        .addField(Tags.MARKET_DEPTH, FULL_BOOK_MARKET_DEPTH)
        .addField(Tags.SUBSCRIPTION_REQUEST_TYPE, SNAPSHOT_AND_UPDATES_REQUEST)

        .addField(Tags.NUMBER_RELATED_SYMBOLS, Integer.toString(this.symbolsToSubscribe.size()));
        for (final String symbol : this.symbolsToSubscribe) {
            this.builder.addField(Tags.SYMBOL, symbol);
        }

        this.builder
        .addField(Tags.NUMBER_MD_ENTRIES, "2")
        .addField(Tags.MD_ENTRY_TYPE, "0")
        .addField(Tags.MD_ENTRY_TYPE, "1");

        try {
            this.fixMessageSender.send(this.builder.build());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
