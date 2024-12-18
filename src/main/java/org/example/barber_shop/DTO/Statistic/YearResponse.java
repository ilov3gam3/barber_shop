package org.example.barber_shop.DTO.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Month;
import java.util.Map;

@Data
@AllArgsConstructor
public class YearResponse {
    @Data
    @AllArgsConstructor
    public static class Booking{
        Integer bookingCount;
        Long Amount;
    }
    private Integer year;
    private Map<Month, Booking> monthlyData;
}
