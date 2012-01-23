package arriba.examples.quotes;

import java.util.Random;
import java.util.Set;

import arriba.common.Sender;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;

public final class RandomQuoteSupplier implements Runnable {

    private final SubscriptionService subscriptionService;
    private final Set<String> symbols;
    private final String senderCompId;
    private final Random random = new Random();
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final RichOutboundFixMessageBuilder builder;

    public RandomQuoteSupplier(final SubscriptionService subscriptionService,
            final Set<String> symbols,
            final String senderCompId,
            final Sender<OutboundFixMessage> fixMessageSender,
            final RichOutboundFixMessageBuilder builder) {
        this.subscriptionService = subscriptionService;
        this.symbols = symbols;
        this.senderCompId = senderCompId;
        this.fixMessageSender = fixMessageSender;
        this.builder = builder;
    }

    @Override
    public void run() {
        for (final String symbol : this.symbols) {
            final double symbolBidPrice = this.random.nextDouble() * this.random.nextInt(25);

            for (final String subscriberCompId : this.subscriptionService.findSubscribers(symbol)) {
                this.builder
                .addStandardHeader(MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, BeginString.FIX44.getValue(), this.senderCompId, subscriberCompId)

                .addField(Tags.SYMBOL, symbol)

                .addField(Tags.NUMBER_MD_ENTRIES, "2")
                .addField(Tags.MD_ENTRY_TYPE, "0")
                .addField(Tags.MD_ENTRY_SIZE, "100")
                .addField(Tags.MD_ENTRY_PRICE, String.valueOf(symbolBidPrice))

                .addField(Tags.MD_ENTRY_TYPE, "1")
                .addField(Tags.MD_ENTRY_SIZE, "200")
                .addField(Tags.MD_ENTRY_PRICE, String.valueOf(symbolBidPrice + 0.05));

                this.fixMessageSender.send(this.builder.build());
            }
        }
    }
}
