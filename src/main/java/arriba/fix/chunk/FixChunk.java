package arriba.fix.chunk;

import java.io.IOException;
import java.io.OutputStream;

public interface FixChunk {

    boolean isDefinedFor(int tag);

    String getValue(int tag);

    @Deprecated
    byte[] toByteArray();

    @Deprecated
    void write(OutputStream outputStream) throws IOException;
}
