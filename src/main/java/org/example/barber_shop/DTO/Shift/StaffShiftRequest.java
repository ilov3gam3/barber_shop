package org.example.barber_shop.DTO.Shift;


import java.time.LocalDate;
import java.util.List;

public class StaffShiftRequest {
    public long staffId;
    public long shiftId;
    public List<LocalDate> dates;
}
