package arriba.fix.chunk;

import arriba.bytearrays.ConcurrentByteArrayKeyedMap;
import cern.colt.map.AbstractIntObjectMap;


public final class CachingFixChunkBuilderSupplier implements FixChunkBuilderSupplier {

    private final FixChunkBuilderSupplier supplier;
    private final ConcurrentByteArrayKeyedMap<FixChunkBuilder> bodyBuilderCache;

    private FixChunkBuilder headerBuilder;
    private FixChunkBuilder trailerBuilder;
    private final AbstractIntObjectMap repeatingGroupBuilderCache;

    public CachingFixChunkBuilderSupplier(final FixChunkBuilderSupplier supplier,
            final ConcurrentByteArrayKeyedMap<FixChunkBuilder> bodyBuilderCache,
            final AbstractIntObjectMap repeatingGroupBuilderCache) {
        this.supplier = supplier;
        this.bodyBuilderCache = bodyBuilderCache;
        this.repeatingGroupBuilderCache = repeatingGroupBuilderCache;
    }

    @Override
    public FixChunkBuilder getBodyBuilder(final byte[] messageType) {
        final FixChunkBuilder cachedBuilder = this.bodyBuilderCache.get(messageType);

        if (null == cachedBuilder) {
            final FixChunkBuilder builder = this.supplier.getBodyBuilder(messageType);
            this.bodyBuilderCache.putIfAbsent(messageType, builder);

            return builder;
        }

        return cachedBuilder;
    }

    @Override
    public FixChunkBuilder getRepeatingGroupBuilder(final int numberOfRepeatingGroupsTag) {
        final FixChunkBuilder cachedBuilder = (FixChunkBuilder) this.repeatingGroupBuilderCache.get(numberOfRepeatingGroupsTag);

        if (null == cachedBuilder) {
            final FixChunkBuilder builder = this.supplier.getRepeatingGroupBuilder(numberOfRepeatingGroupsTag);
            this.repeatingGroupBuilderCache.put(numberOfRepeatingGroupsTag, builder);

            return builder;
        }

        return cachedBuilder;
    }

    @Override
    public FixChunkBuilder getHeaderBuilder() {
        if (null == this.headerBuilder) {
            this.headerBuilder = this.supplier.getHeaderBuilder();
        }

        return this.headerBuilder;
    }

    @Override
    public FixChunkBuilder getTrailerBuilder() {
        if (null == this.trailerBuilder) {
            this.trailerBuilder = this.supplier.getTrailerBuilder();
        }

        return this.trailerBuilder;
    }
}
