package org.example.barber_shop.DTO.Shift;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class StaffShiftResponse {
    private long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
