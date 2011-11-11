package arriba.fix.outbound;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateSupplier {

    private static final String UTC_TIMESTAMP_FORMAT = "yyyyMMdd-HH:mm:ss";

    private DateSupplier() {}

    public static String getUtcTimestamp() {
        return new SimpleDateFormat(UTC_TIMESTAMP_FORMAT).format(new Date());
    }
}
