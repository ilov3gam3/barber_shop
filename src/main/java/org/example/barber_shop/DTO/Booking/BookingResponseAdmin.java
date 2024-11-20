package org.example.barber_shop.DTO.Booking;

import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.DTO.BookingDetail.BookingDetailResponse;
import org.example.barber_shop.DTO.User.UserResponseNoFile;

import java.sql.Timestamp;
import java.util.List;

public class BookingResponseAdmin {
    public long id;
    public BookingStatus status;
    public String note;
    public UserResponseNoFile staff;
    public UserResponseNoFile customer;
    public Timestamp startTime;
    public Timestamp endTime;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public List<BookingDetailResponse> bookingDetails;
}
