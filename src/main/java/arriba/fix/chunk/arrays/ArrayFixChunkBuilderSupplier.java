package arriba.fix.chunk.arrays;

import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.FixChunkBuilderSupplier;
import arriba.fix.tagindexresolvers.TagIndexResolverRepository;

public final class ArrayFixChunkBuilderSupplier implements FixChunkBuilderSupplier {

    private final TagIndexResolverRepository resolverRepository;

    public ArrayFixChunkBuilderSupplier(final TagIndexResolverRepository resolverRepository) {
        this.resolverRepository = resolverRepository;
    }

    @Override
    public FixChunkBuilder getBodyBuilder(final byte[] messageType) {
        return new ArrayFixChunkBuilder(this.resolverRepository.findBodyResolver(messageType));
    }

    @Override
    public FixChunkBuilder getRepeatingGroupBuilder(final int numberOfRepeatingGroupsTag) {
        return new ArrayFixChunkBuilder(this.resolverRepository.findRepeatingGroupResolver(numberOfRepeatingGroupsTag));
    }

    @Override
    public FixChunkBuilder getHeaderBuilder() {
        return new ArrayFixChunkBuilder(this.resolverRepository.findHeaderResolver());
    }

    @Override
    public FixChunkBuilder getTrailerBuilder() {
        return new ArrayFixChunkBuilder(this.resolverRepository.findTrailerResolver());
    }
}
