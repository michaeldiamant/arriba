package arriba.fix.chunk;

import arriba.bytearrays.ConcurrentByteArrayKeyedMap;


public final class CachingFixChunkBuilderSupplier implements FixChunkBuilderSupplier {

    private final FixChunkBuilderSupplier supplier;
    private final ConcurrentByteArrayKeyedMap<FixChunkBuilder> bodyBuilderCache;

    private FixChunkBuilder headerBuilder;
    private FixChunkBuilder trailerBuilder;

    public CachingFixChunkBuilderSupplier(final FixChunkBuilderSupplier supplier,
            final ConcurrentByteArrayKeyedMap<FixChunkBuilder> bodyBuilderCache) {
        this.supplier = supplier;
        this.bodyBuilderCache = bodyBuilderCache;
    }

    @Override
    public FixChunkBuilder getBodyBuilder(final byte[] messageType) {
        final FixChunkBuilder cachedBuilder = this.bodyBuilderCache.get(messageType);

        // TODO Either implement a Cache interface or use Guave Cache interface to hide caching.
        if (null == cachedBuilder) {
            final FixChunkBuilder builder = this.supplier.getBodyBuilder(messageType);
            this.bodyBuilderCache.putIfAbsent(messageType, builder);

            return builder;
        }

        return cachedBuilder;
    }

    @Override
    public FixChunkBuilder getRepeatingGroupBuilder(final int numberOfRepeatingGroupsTag) {
        // TODO Caching strategy.
        return this.supplier.getRepeatingGroupBuilder(numberOfRepeatingGroupsTag);
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
