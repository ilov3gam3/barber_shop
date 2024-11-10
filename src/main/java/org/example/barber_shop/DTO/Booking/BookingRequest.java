package org.example.barber_shop.DTO.Booking;

import java.sql.Timestamp;
import java.util.List;

public class BookingRequest {
    private long staff_id;
    private String note;
    private Timestamp startTime;
    private List<Long> serviceIds;
    private List<Long> comboIds;
}
