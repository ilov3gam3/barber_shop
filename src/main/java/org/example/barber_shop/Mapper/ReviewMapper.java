package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Review.ReviewResponse;
import org.example.barber_shop.DTO.Review.ReviewResponseStaff;
import org.example.barber_shop.Entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "details", target = "reviewDetails")
    @Mapping(source = "booking.id", target = "bookingId")
    ReviewResponse toReviewResponse(Review review);
    List<ReviewResponse> toReviewResponses(List<Review> reviews);
    @Mapping(source = "booking.id", target = "bookingId")
    ReviewResponseStaff toReviewResponseStaff(Review review);
    List<ReviewResponseStaff> toReviewResponseStaffs(List<Review> reviews);
}
