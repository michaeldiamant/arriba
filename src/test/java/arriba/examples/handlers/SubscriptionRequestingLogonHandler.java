package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.RepeatingGroupBuilder;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.Logon;

public final class SubscriptionRequestingLogonHandler implements Handler<Logon> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";
    private static final String FULL_BOOK_MARKET_DEPTH = "0";
    private static final String SNAPSHOT_AND_UPDATES_REQUEST = "1";

    private final Set<String> symbolsToSubscribe;
    private final Sender<InboundFixMessage> fixMessageSender;
    private final InboundFixMessageBuilder inboundFixMessageBuilder;
    private final RepeatingGroupBuilder repeatingGroupBuilder = new RepeatingGroupBuilder();
    private final AtomicInteger messageCount;

    public SubscriptionRequestingLogonHandler(final Set<String> symbolsToSubscribe,
            final Sender<InboundFixMessage> fixMessageSender, final InboundFixMessageBuilder inboundFixMessageBuilder,
            final AtomicInteger messageCount) {
        this.symbolsToSubscribe = symbolsToSubscribe;
        this.fixMessageSender = fixMessageSender;
        this.inboundFixMessageBuilder = inboundFixMessageBuilder;
        this.messageCount = messageCount;
    }

    @Override
    public void handle(final Logon message) {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.inboundFixMessageBuilder.addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.incrementAndGet()));
        this.inboundFixMessageBuilder.setMessageType("V");
        this.inboundFixMessageBuilder.setBeginStringBytes(BeginString.FIXT11);
        this.inboundFixMessageBuilder.addField(Tags.SENDER_COMP_ID, message.getTargetCompId());
        this.inboundFixMessageBuilder.addField(Tags.TARGET_COMP_ID, message.getSenderCompId());
        this.inboundFixMessageBuilder.addField(Tags.SENDING_TIME, sdf.format(new Date()));

        // TODO Consolidate the standard header tags boiler plate logic.

        this.inboundFixMessageBuilder.addField(Tags.MD_REQUEST_ID, String.valueOf(this.messageCount.get()));
        this.inboundFixMessageBuilder.addField(Tags.MARKET_DEPTH, FULL_BOOK_MARKET_DEPTH);
        this.inboundFixMessageBuilder.addField(Tags.SUBSCRIPTION_REQUEST_TYPE, SNAPSHOT_AND_UPDATES_REQUEST);

        this.repeatingGroupBuilder.setNumberOfRepeatingGroups(this.symbolsToSubscribe.size());
        this.repeatingGroupBuilder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS);
        for (final String symbol : this.symbolsToSubscribe) {
            this.repeatingGroupBuilder.addField(Tags.SYMBOL, symbol);
        }

        this.repeatingGroupBuilder.setNumberOfRepeatingGroups(2);
        this.repeatingGroupBuilder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES);
        this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_TYPE, "0");
        this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_TYPE, "1");

        this.inboundFixMessageBuilder.setRepeatingGroups(this.repeatingGroupBuilder.build());

        try {
            this.fixMessageSender.send(this.inboundFixMessageBuilder.build());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        this.repeatingGroupBuilder.clear();
        this.inboundFixMessageBuilder.clear();
    }

}
