package org.example.barber_shop.DTO.Review;


import java.util.List;

public class ReviewAddRequest {
    public long bookingId;
    public String staffComment;
    public int staffRating;
    public List<ReviewDetailAddRequest> reviewDetails;
}
