package org.example.barber_shop.DTO.Review;

import java.util.List;

public class ReviewUpdateRequest {
    public long id;
    public String staffComment;
    public int staffRating;
    public List<ReviewDetailUpdateRequest> reviewDetails;
}
