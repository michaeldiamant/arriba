package arriba.transport;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public final class InMemoryTransportRepository<ID, T> implements TransportRepository<ID, T> {

    // TODO Use MapMaker
    private final ConcurrentMap<ID, Transport<T>> idToChannel = Maps.newConcurrentMap();

    @Override
    public Transport<T> add(final ID id, final Transport<T> transport) {
        return this.idToChannel.putIfAbsent(id, transport);
    }

    @Override
    public boolean remove(final Transport<T> transport) {
        final Iterator<Entry<ID, Transport<T>>> iterator = this.idToChannel.entrySet().iterator();
        while (iterator.hasNext()) {
            if (transport == iterator.next().getValue()) {
                iterator.remove();

                return true;
            }
        }

        return false;
    }

    @Override
    public Transport<T> find(final ID id) {
        return this.idToChannel.get(id);
    }
}
