package arriba.fix.chunk;

import java.io.IOException;
import java.io.OutputStream;

public interface FixChunk {

    String getValue(int tag);

    byte[] toByteArray();

    void write(OutputStream outputStream) throws IOException;
}
