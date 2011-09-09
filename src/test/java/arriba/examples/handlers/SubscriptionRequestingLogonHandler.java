package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.FixMessageBuilder;
import arriba.fix.RepeatingGroupBuilder;
import arriba.fix.Tags;
import arriba.fix.chunk.arrays.ArrayFixChunk;
import arriba.fix.fields.BeginString;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.Logon;

public final class SubscriptionRequestingLogonHandler implements Handler<Logon> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";
    private static final String FULL_BOOK_MARKET_DEPTH = "0";
    private static final String SNAPSHOT_AND_UPDATES_REQUEST = "1";

    private final Set<String> symbolsToSubscribe;
    private final Sender<FixMessage> fixMessageSender;
    private final FixMessageBuilder<ArrayFixChunk> fixMessageBuilder;
    private final RepeatingGroupBuilder repeatingGroupBuilder = new RepeatingGroupBuilder();
    private final AtomicInteger messageCount;

    public SubscriptionRequestingLogonHandler(final Set<String> symbolsToSubscribe,
            final Sender<FixMessage> fixMessageSender, final FixMessageBuilder<ArrayFixChunk> fixMessageBuilder,
            final AtomicInteger messageCount) {
        this.symbolsToSubscribe = symbolsToSubscribe;
        this.fixMessageSender = fixMessageSender;
        this.fixMessageBuilder = fixMessageBuilder;
        this.messageCount = messageCount;
    }

    @Override
    public void handle(final Logon message) {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.fixMessageBuilder.addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.incrementAndGet()));
        this.fixMessageBuilder.setMessageType("V");
        this.fixMessageBuilder.setBeginStringBytes(BeginString.FIXT11);
        this.fixMessageBuilder.addField(Tags.SENDER_COMP_ID, message.getTargetCompId());
        this.fixMessageBuilder.addField(Tags.TARGET_COMP_ID, message.getSenderCompId());
        this.fixMessageBuilder.addField(Tags.SENDING_TIME, sdf.format(new Date()));

        // TODO Consolidate the standard header tags boiler plate logic.

        this.fixMessageBuilder.addField(Tags.MD_REQUEST_ID, String.valueOf(this.messageCount.get()));
        this.fixMessageBuilder.addField(Tags.MARKET_DEPTH, FULL_BOOK_MARKET_DEPTH);
        this.fixMessageBuilder.addField(Tags.SUBSCRIPTION_REQUEST_TYPE, SNAPSHOT_AND_UPDATES_REQUEST);

        this.repeatingGroupBuilder.setNumberOfRepeatingGroups(this.symbolsToSubscribe.size());
        this.repeatingGroupBuilder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS);
        for (final String symbol : this.symbolsToSubscribe) {
            this.repeatingGroupBuilder.addField(Tags.SYMBOL, symbol);
        }

        this.repeatingGroupBuilder.setNumberOfRepeatingGroups(2);
        this.repeatingGroupBuilder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES);
        this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_TYPE, "0");
        this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_TYPE, "1");

        this.fixMessageBuilder.setRepeatingGroups(this.repeatingGroupBuilder.build());

        this.fixMessageBuilder.build();

        try {
            this.fixMessageSender.send(this.fixMessageBuilder.build());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        this.fixMessageBuilder.clear();
    }

}
