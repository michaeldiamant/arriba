package arriba.examples.quotes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Sender;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.RepeatingGroupBuilder;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessage;

public final class RandomQuoteSupplier implements Runnable {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final SubscriptionService subscriptionService;
    private final Set<String> symbols;
    private final InboundFixMessageBuilder inboundFixMessageBuilder;
    private final RepeatingGroupBuilder repeatingGroupBuilder = new RepeatingGroupBuilder();
    private final AtomicInteger messageCount;
    private final String senderCompId;
    private final Random random = new Random();
    private final Sender<InboundFixMessage> fixMessageSender;

    public RandomQuoteSupplier(final SubscriptionService subscriptionService, final Set<String> symbols,
            final InboundFixMessageBuilder inboundFixMessageBuilder, final AtomicInteger messageCount,
            final String senderCompId, final Sender<InboundFixMessage> fixMessageSender) {
        this.subscriptionService = subscriptionService;
        this.symbols = symbols;
        this.inboundFixMessageBuilder = inboundFixMessageBuilder;
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
                this.inboundFixMessageBuilder.addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.incrementAndGet()));
                this.inboundFixMessageBuilder.setMessageType("W");
                this.inboundFixMessageBuilder.setBeginStringBytes(BeginString.FIXT11);
                this.inboundFixMessageBuilder.addField(Tags.SENDER_COMP_ID, this.senderCompId);
                this.inboundFixMessageBuilder.addField(Tags.TARGET_COMP_ID, subscriberCompId);
                this.inboundFixMessageBuilder.addField(Tags.SENDING_TIME, sdf.format(new Date()));

                this.inboundFixMessageBuilder.addField(Tags.SYMBOL, symbol);

                this.repeatingGroupBuilder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES);
                this.repeatingGroupBuilder.setNumberOfRepeatingGroups(2);

                this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_TYPE, "0");
                this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_SIZE, "100");
                this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_PRICE, String.valueOf(symbolBidPrice));


                this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_TYPE, "1");
                this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_SIZE, "200");
                this.repeatingGroupBuilder.addField(Tags.MD_ENTRY_PRICE, String.valueOf(symbolBidPrice + 0.05));

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
    }
}
