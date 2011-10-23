package arriba.fix.chunk;

public interface FixChunkBuilder {

    FixChunkBuilder addField(int tag, byte[] value);

    FixChunk build();

    void clear();
}
