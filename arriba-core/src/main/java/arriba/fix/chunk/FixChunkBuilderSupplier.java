package arriba.fix.chunk;

public interface FixChunkBuilderSupplier {

    FixChunkBuilder getBodyBuilder(byte[] messageType);

    FixChunkBuilder getRepeatingGroupBuilder(int numberOfRepeatingGroupsTag);

    FixChunkBuilder getHeaderBuilder();

    FixChunkBuilder getTrailerBuilder();
}
