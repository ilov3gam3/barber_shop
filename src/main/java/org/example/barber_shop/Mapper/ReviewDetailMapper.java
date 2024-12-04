package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Review.ReviewDetailResponse;
import org.example.barber_shop.Entity.Review;
import org.example.barber_shop.Entity.ReviewDetail;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewDetailMapper {
    ReviewDetailResponse toReviewDetailResponse(ReviewDetail review);
    List<ReviewDetailResponse> toReviewDetailResponseList(List<ReviewDetail> reviews);
}
