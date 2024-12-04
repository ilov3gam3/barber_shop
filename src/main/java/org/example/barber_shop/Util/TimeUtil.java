package org.example.barber_shop.Util;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.WeekFields;

public class TimeUtil {
    public static Timestamp calculateEndTime(Timestamp startTime, int minutesToAdd) {
        Instant endInstant = startTime.toInstant().plus(Duration.ofMinutes(minutesToAdd));
        return Timestamp.from(endInstant);
    }
    public static LocalDateTime getLastWeekStartDate() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        return lastMonday.atStartOfDay();
    }

    public static LocalDateTime getLastWeekEndDate() {
        LocalDate today = LocalDate.now();
        LocalDate lastSunday = today.minusWeeks(1).with(DayOfWeek.MONDAY).plusDays(6);
        return lastSunday.atTime(LocalTime.MAX);
    }



    public static LocalDate getStartOfWeek(int week, int year) {
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        WeekFields weekFields = WeekFields.ISO;
        return firstDayOfYear.with(weekFields.weekOfYear(), week).with(weekFields.dayOfWeek(), DayOfWeek.MONDAY.getValue());
    }
    public static LocalDate getEndOfWeek(int week, int year) {
        return getStartOfWeek(week, year).plusDays(6);
    }
}
