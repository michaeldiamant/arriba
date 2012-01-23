package arriba.fix.outbound;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DateSupplier {

    private static final String UTC_TIMESTAMP_FORMAT = "yyyyMMdd-HH:mm:ss";

    private DateSupplier() {}

    public static String getUtcTimestamp() {
        final SimpleDateFormat sdf = new SimpleDateFormat(UTC_TIMESTAMP_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
