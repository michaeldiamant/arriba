package arriba.fix.chunk;


public final class CachingFixChunkBuilderSupplier implements FixChunkBuilderSupplier {

    private final FixChunkBuilderSupplier supplier;

    private FixChunkBuilder headerBuilder;
    private FixChunkBuilder trailerBuilder;


    public CachingFixChunkBuilderSupplier(final FixChunkBuilderSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public FixChunkBuilder getBodyBuilder(final byte[] messageType) {
        // TODO Implement body builder caching strategy.
        return this.supplier.getBodyBuilder(messageType);
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
