package maks.molch.dmitr.badminton_service.util;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.Date;

public class TimeUtils {

    private TimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Date toDate(OffsetDateTime odt) {
        Instant instant = odt.toInstant();
        return Date.from(instant);
    }
}
