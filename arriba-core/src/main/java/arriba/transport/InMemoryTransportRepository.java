package arriba.transport;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public final class InMemoryTransportRepository<ID, T> implements TransportRepository<ID, T> {

    // TODO Use MapMaker
    private final ConcurrentMap<ID, Transport<T>> idToTransport = Maps.newConcurrentMap();
    private final TransportFactory<T> factory;

    public InMemoryTransportRepository(final TransportFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Transport<T> add(final ID id, final TransportIdentity<T> identity) {
        return this.idToTransport.putIfAbsent(id, this.factory.create(identity));
    }

    @Override
    public boolean remove(final TransportIdentity<T> identity) {
        // Compare using TransportIdentity to avoid creating Transport instance.
        final Iterator<Entry<ID, Transport<T>>> iterator = this.idToTransport.entrySet().iterator();
        while (iterator.hasNext()) {
            if (identity.equals(iterator.next().getValue().getIdentity())) {
                iterator.remove();

                return true;
            }
        }

        return false;
    }

    @Override
    public Transport<T> find(final ID id) {
        return this.idToTransport.get(id);
    }

    @Override
    public ID find(TransportIdentity<T> identity) {
        // Compare using TransportIdentity to avoid creating Transport instance.
        final Iterator<Entry<ID, Transport<T>>> iterator = this.idToTransport.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<ID, Transport<T>> entry = iterator.next();
            if (identity.equals(entry.getValue().getIdentity())) {
                return entry.getKey();
            }
        }        
        
        return null;
    }
}
