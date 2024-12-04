package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Review.ReviewAddRequest;
import org.example.barber_shop.DTO.Review.ReviewDetailUpdateRequest;
import org.example.barber_shop.DTO.Review.ReviewUpdateRequest;
import org.example.barber_shop.Service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping("")
    public ApiResponse<?> addReview(@RequestBody ReviewAddRequest reviewAddRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "REVIEW ADDED", reviewService.addReview(reviewAddRequest)
        );
    }

    @PutMapping("")
    public ApiResponse<?> updateReview(@RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "REVIEW UPDATED", reviewService.updateReview(reviewUpdateRequest)
        );
    }
    @PutMapping("/review-detail")
    public ApiResponse<?> updateReviewDetail(@RequestBody ReviewDetailUpdateRequest reviewDetailUpdateRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "REVIEW DETAIL UPDATED", reviewService.updateReviewDetail(reviewDetailUpdateRequest)
        );
    }
    @GetMapping("/all")
    public ApiResponse<?> getAllReviews(){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL REVIEWS", reviewService.getAllReviews()
        );
    }
    @GetMapping("/staff-review/{id}")
    public ApiResponse<?> getStaffReviews(@PathVariable long id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF REVIEW", reviewService.getStaffReview(id)
        );
    }
    @GetMapping("/combo/{id}")
    public ApiResponse<?> getComboReviews(@PathVariable long id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "COMBO REVIEWS", reviewService.getComboReviews(id)
        );
    }
    @GetMapping("/service/{id}")
    public ApiResponse<?> getServiceReviews(@PathVariable long id){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "SERVICE REVIEWS", reviewService.getServiceReviews(id)
        );
    }
}
