package org.example.barber_shop.DTO.Review;

import java.sql.Timestamp;
import java.util.List;

public class ReviewResponse {
    public long id;
    public long bookingId;
    public String staffComment;
    public int staffRating;
    public List<ReviewDetailResponse> reviewDetails;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
