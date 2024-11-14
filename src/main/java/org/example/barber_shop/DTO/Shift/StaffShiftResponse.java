package org.example.barber_shop.DTO.Shift;

import org.example.barber_shop.DTO.User.UserResponseNoFile;
import org.example.barber_shop.Entity.Shift;

import java.time.LocalDate;
import java.time.LocalTime;

public class StaffShiftResponse {
    public Shift shift;
    public UserResponseNoFile staff;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
