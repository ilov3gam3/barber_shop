package org.example.barber_shop.DTO.Booking;


import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.DTO.BookingDetail.BookingDetailResponse;
import org.example.barber_shop.DTO.User.UserResponse;

import java.sql.Timestamp;
import java.util.List;

public class BookingResponseNoUser {
    public long id;
    public BookingStatus status;
    public String note;
    public UserResponse staff;
    public Timestamp startTime;
    public Timestamp endTime;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public List<BookingDetailResponse> bookingDetails;
}
