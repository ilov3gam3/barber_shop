package org.example.barber_shop.DTO.Review;

import java.sql.Timestamp;

public class ReviewResponseStaff {
    public long id;
    public long bookingId;
    public String staffComment;
    public int staffRating;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
