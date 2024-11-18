package org.example.barber_shop.DTO.Shift;

import java.time.LocalTime;

public class ShiftRequest {
    public long id;
    public String name;
    public LocalTime startTime;
    public LocalTime endTime;
}
