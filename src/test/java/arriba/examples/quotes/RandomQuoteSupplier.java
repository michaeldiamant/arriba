package arriba.examples.quotes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Sender;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RawOutboundFixMessageBuilder;

public final class RandomQuoteSupplier implements Runnable {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final SubscriptionService subscriptionService;
    private final Set<String> symbols;
    private final RawOutboundFixMessageBuilder builder = new RawOutboundFixMessageBuilder();

    private final AtomicInteger messageCount;
    private final String senderCompId;
    private final Random random = new Random();
    private final Sender<OutboundFixMessage> fixMessageSender;

    public RandomQuoteSupplier(final SubscriptionService subscriptionService, final Set<String> symbols,
            final AtomicInteger messageCount,
            final String senderCompId, final Sender<OutboundFixMessage> fixMessageSender) {
        this.subscriptionService = subscriptionService;
        this.symbols = symbols;
        this.messageCount = messageCount;
        this.senderCompId = senderCompId;
        this.fixMessageSender = fixMessageSender;
    }

    @Override
    public void run() {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        for (final String symbol : this.symbols) {
            final double symbolBidPrice = this.random.nextDouble() * this.random.nextInt(25);

            for (final String subscriberCompId : this.subscriptionService.findSubscribers(symbol)) {
                this.builder
                .addField(Tags.BEGIN_STRING, BeginString.FIXT11.getValue())
                .addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.incrementAndGet()))
                .addField(Tags.MESSAGE_TYPE, "W")
                .addField(Tags.SENDER_COMP_ID, this.senderCompId)
                .addField(Tags.TARGET_COMP_ID, subscriberCompId)
                .addField(Tags.SENDING_TIME, sdf.format(new Date()))

                .addField(Tags.SYMBOL, symbol)

                .addField(Tags.NUMBER_MD_ENTRIES, "2")
                .addField(Tags.MD_ENTRY_TYPE, "0")
                .addField(Tags.MD_ENTRY_SIZE, "100")
                .addField(Tags.MD_ENTRY_PRICE, String.valueOf(symbolBidPrice))

                .addField(Tags.MD_ENTRY_TYPE, "1")
                .addField(Tags.MD_ENTRY_SIZE, "200")
                .addField(Tags.MD_ENTRY_PRICE, String.valueOf(symbolBidPrice + 0.05));

                try {
                    this.fixMessageSender.send(this.builder.build());
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
