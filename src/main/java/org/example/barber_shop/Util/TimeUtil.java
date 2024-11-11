package org.example.barber_shop.Util;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class TimeUtil {
    public static Timestamp calculateEndTime(Timestamp startTime, int minutesToAdd) {
        Instant endInstant = startTime.toInstant().plus(Duration.ofMinutes(minutesToAdd));
        return Timestamp.from(endInstant);
    }
}
