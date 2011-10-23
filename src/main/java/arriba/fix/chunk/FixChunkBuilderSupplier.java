package arriba.fix.chunk;

public interface FixChunkBuilderSupplier {

    FixChunkBuilder getBodyBuilder(byte[] messageType);

    FixChunkBuilder getHeaderBuilder();

    FixChunkBuilder getTrailerBuilder();
}
