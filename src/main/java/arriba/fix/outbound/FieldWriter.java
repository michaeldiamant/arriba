package arriba.fix.outbound;

import java.io.IOException;
import java.io.OutputStream;

import arriba.fix.Fields;

public final class FieldWriter {

    private FieldWriter() {}

    /**
     * Tag is expected to have equal sign provided (e.g. 52=).
     */
    public static int write(final byte[] tag, final String value, final OutputStream out) throws IOException {
        int bytesSum = 0;

        out.write(tag);
        bytesSum += calculateSum(tag);

        final int valueLength = value.length();
        for (int valueIndex = 0; valueIndex < valueLength; valueIndex++) {
            final byte byteValue = (byte) value.charAt(valueIndex);

            out.write(byteValue);
            bytesSum += byteValue;
        }

        out.write(Fields.DELIMITER);
        bytesSum += Fields.DELIMITER;

        return bytesSum;
    }

    private static int calculateSum(final byte[] bytes) {
        int sum = 0;
        final int bytesLength = bytes.length;
        for (int bytesIndex = 0; bytesIndex < bytesLength; bytesIndex++) {
            sum += bytes[bytesIndex];
        }

        return sum;
    }

}
