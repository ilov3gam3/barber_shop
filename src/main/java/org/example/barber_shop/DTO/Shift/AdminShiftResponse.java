package org.example.barber_shop.DTO.Shift;

import lombok.Getter;
import lombok.Setter;
import org.example.barber_shop.DTO.User.UserResponse;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AdminShiftResponse {
    private long id;
    private UserResponse staff;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
