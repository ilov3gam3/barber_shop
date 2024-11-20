package org.example.barber_shop.DTO.Booking;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;

public class BookingUpdateRequest {
    public long bookingId;
    public long staff_id;
    public String note;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Timestamp startTime;
    public List<Long> serviceIds;
    public List<Long> comboIds;
}
