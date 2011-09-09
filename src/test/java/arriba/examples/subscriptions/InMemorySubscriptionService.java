package arriba.examples.subscriptions;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public final class InMemorySubscriptionService implements SubscriptionService {

    private final ReadWriteLock subscriptionsLock = new ReentrantReadWriteLock();
    private final Multimap<String, String> symbolToTargetCompIds = HashMultimap.create();

    @Override
    public void addSubscriptions(final String targetCompId, final Set<String> symbols) {
        this.subscriptionsLock.writeLock().lock();
        try {
            for (final String symbol : symbols) {
                this.symbolToTargetCompIds.get(symbol).add(targetCompId);
            }
        } finally {
            this.subscriptionsLock.writeLock().unlock();
        }
    }

    @Override
    public void removeSubscription(final String targetCompId) {
        this.subscriptionsLock.writeLock().lock();
        try {
            final Iterator<String> compIdIterator = this.symbolToTargetCompIds.values().iterator();
            while (compIdIterator.hasNext()) {
                if (compIdIterator.next().equals(targetCompId)) {
                    compIdIterator.remove();
                }
            }
        } finally {
            this.subscriptionsLock.writeLock().unlock();
        }
    }

    @Override
    public Set<String> findSubscribers(final String symbol) {
        this.subscriptionsLock.readLock().lock();
        try {
            return Sets.newHashSet(this.symbolToTargetCompIds.get(symbol));
        } finally {
            this.subscriptionsLock.readLock().unlock();
        }
    }
}
