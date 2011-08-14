package arriba.fix.chunk;

public interface FixChunkBuilder<C extends FixChunk> {

    FixChunkBuilder<C> addField(int tag, String value);

    C build();

    void clear();
}
