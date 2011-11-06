package arriba.fix.chunk;


public interface FixChunk {

    boolean isDefinedFor(int tag);

    String getValue(int tag);

    byte[] getSerializedValue(int tag);
}
