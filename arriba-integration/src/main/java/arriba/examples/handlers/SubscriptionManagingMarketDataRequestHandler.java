package arriba.examples.handlers;

import java.util.Set;

import arriba.common.Handler;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.inbound.messages.MarketDataRequest;

import com.google.common.collect.Sets;

public final class SubscriptionManagingMarketDataRequestHandler implements Handler<MarketDataRequest> {

    private static final String SUBSCRIPTION_REQUEST_TYPE = "1";
    private static final String UNSUBSCRIPTION_REQUEST_TYPE = "2";

    private final SubscriptionService subscriptionService;

    public SubscriptionManagingMarketDataRequestHandler(final SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void handle(final MarketDataRequest message) {
        if (SUBSCRIPTION_REQUEST_TYPE.equals(message.getSubscriptionRequestType())) {
            System.out.println("Adding subscription");
            final Set<String> symbols = this.toSymbols(message.getRelatedSymbols());
            this.subscriptionService.addSubscriptions(message.getSenderCompId(), symbols);
        } else if (UNSUBSCRIPTION_REQUEST_TYPE.equals(message.getSubscriptionRequestType())) {
            System.out.println("Removing subscription");
            this.subscriptionService.removeSubscription(message.getSenderCompId());
        } else {
            System.out.println("Unknown request type:  " + message.getSubscriptionRequestType());
        }
    }

    private Set<String> toSymbols(final FixChunk[] relatedSymbols) {
        final Set<String> symbols = Sets.newHashSet();

        for (final FixChunk group : relatedSymbols) {
            symbols.add(group.getValue(Tags.SYMBOL));
        }

        return symbols;
    }
}
