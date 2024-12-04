package org.example.barber_shop.DTO.Booking;

import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.DTO.BookingDetail.BookingDetailResponse;
import org.example.barber_shop.DTO.Review.ReviewResponse;
import org.example.barber_shop.DTO.User.UserResponse;

import java.sql.Timestamp;
import java.util.List;

public class BookingResponseNoStaff {
    public long id;
    public BookingStatus status;
    public String note;
    public UserResponse customer;
    public Timestamp startTime;
    public Timestamp endTime;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public List<BookingDetailResponse> bookingDetails;
    public ReviewResponse review;
}
