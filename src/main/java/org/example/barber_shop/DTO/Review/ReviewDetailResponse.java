package org.example.barber_shop.DTO.Review;

import org.example.barber_shop.Constants.ReviewDetailType;
import org.example.barber_shop.DTO.BookingDetail.BookingDetailResponse;

import java.sql.Timestamp;

public class ReviewDetailResponse {
    public long id;
    public BookingDetailResponse bookingDetail;
    public String comment;
    public int rating;
    public ReviewDetailType type;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
