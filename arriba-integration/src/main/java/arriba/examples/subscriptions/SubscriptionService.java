package arriba.examples.subscriptions;

import java.util.Set;

public interface SubscriptionService {

    void addSubscriptions(String targetCompId, Set<String> symbols);

    void removeSubscription(String targetCompId);

    Set<String> findSubscribers(String symbol);
}
