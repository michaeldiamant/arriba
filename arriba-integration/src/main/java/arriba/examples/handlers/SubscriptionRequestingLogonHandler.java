package arriba.examples.handlers;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.messages.Logon;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;

public final class SubscriptionRequestingLogonHandler implements Handler<Logon> {

    private static final String FULL_BOOK_MARKET_DEPTH = "0";
    private static final String SNAPSHOT_AND_UPDATES_REQUEST = "1";

    private final Set<String> symbolsToSubscribe;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final RichOutboundFixMessageBuilder builder;
    private final AtomicLong mdRequestIdGenerator = new AtomicLong();

    public SubscriptionRequestingLogonHandler(final Set<String> symbolsToSubscribe,
            final Sender<OutboundFixMessage> fixMessageSender,
            final RichOutboundFixMessageBuilder builder) {
        this.symbolsToSubscribe = symbolsToSubscribe;
        this.fixMessageSender = fixMessageSender;
        this.builder = builder;
    }

    @Override
    public void handle(final Logon message) {
        this.builder
        .addStandardHeader(MessageType.MARKET_DATA_REQUEST, message)

        .addField(Tags.MD_REQUEST_ID, String.valueOf(this.mdRequestIdGenerator.getAndIncrement()))
        .addField(Tags.MARKET_DEPTH, FULL_BOOK_MARKET_DEPTH)
        .addField(Tags.SUBSCRIPTION_REQUEST_TYPE, SNAPSHOT_AND_UPDATES_REQUEST)

        .addField(Tags.NUMBER_RELATED_SYMBOLS, Integer.toString(this.symbolsToSubscribe.size()));
        for (final String symbol : this.symbolsToSubscribe) {
            this.builder.addField(Tags.SYMBOL, symbol);
        }

        this.builder
        .addField(Tags.NUMBER_MD_ENTRY_TYPES, "2")
        .addField(Tags.MD_ENTRY_TYPE, "0")
        .addField(Tags.MD_ENTRY_TYPE, "1");

        this.fixMessageSender.send(this.builder.build());
    }
}
