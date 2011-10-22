package arriba.fix.chunk;

public interface FixChunkBuilder {

    FixChunkBuilder addField(int tag, String value);

    FixChunk build();

    void clear();
}
