package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.ReviewDetailType;
import org.example.barber_shop.DTO.Review.*;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Mapper.ReviewDetailMapper;
import org.example.barber_shop.Mapper.ReviewMapper;
import org.example.barber_shop.Repository.BookingRepository;
import org.example.barber_shop.Repository.ReviewDetailRepository;
import org.example.barber_shop.Repository.ReviewRepository;
import org.example.barber_shop.Util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewDetailRepository reviewDetailRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewDetailMapper reviewDetailMapper;

    public List<ReviewResponse> getAllReviews() {
        return reviewMapper.toReviewResponses(reviewRepository.findAll());
    }

    public ReviewResponse addReview(ReviewAddRequest reviewAddRequest){
        User customer = SecurityUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndCustomer(reviewAddRequest.bookingId, customer);
        System.out.println(booking.getStatus());
        if(booking != null){
            if (booking.getStatus() == BookingStatus.COMPLETED){
                Review review = new Review();
                review.setBooking(booking);
                review.setStaffComment(reviewAddRequest.staffComment);
                review.setStaffRating(reviewAddRequest.staffRating);
                if (areAllBookingDetailsReviewed(booking.getBookingDetails(), reviewAddRequest.reviewDetails)){
                    List<ReviewDetail> reviewDetails = new ArrayList<>();
                    for (int i = 0; i < reviewAddRequest.reviewDetails.size(); i++) {
                        ReviewDetail tempReviewDetail = new ReviewDetail();
                        tempReviewDetail.setBookingDetail(findBookingDetailWithId(reviewAddRequest.reviewDetails.get(i).bookingDetailId, booking.getBookingDetails()));
                        tempReviewDetail.setReview(review);
                        tempReviewDetail.setComment(reviewAddRequest.reviewDetails.get(i).comment);
                        tempReviewDetail.setRating(reviewAddRequest.reviewDetails.get(i).rating);
                        if (tempReviewDetail.getBookingDetail().getService() == null){
                            tempReviewDetail.setType(ReviewDetailType.COMBO);
                        } else {
                            tempReviewDetail.setType(ReviewDetailType.SERVICE);
                        }
                        reviewDetails.add(tempReviewDetail);
                    }
                    review.setDetails(reviewDetails);
                    return reviewMapper.toReviewResponse(reviewRepository.save(review));
                } else {
                    throw new LocalizedException("review.invalid.booking.detail.id");
                }
            } else {
                throw new LocalizedException("review.invalid.booking");
            }
        } else {
            throw new LocalizedException("booking.not.found");
        }
    }
    public BookingDetail findBookingDetailWithId(long id, List<BookingDetail> bookingDetails){
        for (BookingDetail bookingDetail : bookingDetails) {
            if (bookingDetail.getId() == id) {
                return bookingDetail;
            }
        }
        return null;
    }
    public static boolean areAllBookingDetailsReviewed(List<BookingDetail> bookingDetails, List<ReviewDetailAddRequest> reviewDetails){
        Set<Long> bookingDetailIds = bookingDetails.stream()
                .map(BookingDetail::getId)
                .collect(Collectors.toSet());
        Set<Long> reviewedBookingIds = reviewDetails.stream()
                .map(reviewDetail -> reviewDetail.bookingDetailId)
                .collect(Collectors.toSet());
        return reviewedBookingIds.containsAll(bookingDetailIds);
    }

    public ReviewResponse updateReview(ReviewUpdateRequest reviewUpdateRequest){
        User customer = SecurityUtils.getCurrentUser();
        Review review = reviewRepository.findByIdAndBooking_Customer(reviewUpdateRequest.id, customer);
        if(review != null){
            review.setStaffComment(reviewUpdateRequest.staffComment);
            review.setStaffRating(reviewUpdateRequest.staffRating);
            for (int i = 0; i < review.getDetails().size(); i++) {
                int finalI = i;
                Optional<ReviewDetailUpdateRequest> reviewDetailUpdateRequestOptional = reviewUpdateRequest.reviewDetails.stream().filter(obj -> obj.id == review.getDetails().get(finalI).getId()).findFirst();
                if(reviewDetailUpdateRequestOptional.isPresent()){
                    review.getDetails().get(i).setRating(reviewDetailUpdateRequestOptional.get().rating);
                    review.getDetails().get(i).setComment(reviewDetailUpdateRequestOptional.get().comment);
                } else {
                    throw new LocalizedException("review.invalid.booking.detail.id");
                }
            }
            return reviewMapper.toReviewResponse(reviewRepository.save(review));
        } else {
            throw new LocalizedException("review.invalid.id");
        }
    }
    public ReviewDetailResponse updateReviewDetail(ReviewDetailUpdateRequest reviewDetailUpdateRequest){
        User customer = SecurityUtils.getCurrentUser();
        ReviewDetail reviewDetail = reviewDetailRepository.findByIdAndReview_Booking_Customer(reviewDetailUpdateRequest.id, customer);
        if (reviewDetail != null) {
            reviewDetail.setRating(reviewDetailUpdateRequest.rating);
            reviewDetail.setComment(reviewDetailUpdateRequest.comment);
            return reviewDetailMapper.toReviewDetailResponse(reviewDetailRepository.save(reviewDetail));
        } else {
            throw new LocalizedException("review.detail.invalid.id");
        }
    }
    public List<ReviewResponseStaff> getStaffReview(long id){
        List<Review> reviews = reviewRepository.findByBooking_Staff_Id(id);
        return reviewMapper.toReviewResponseStaffs(reviews);
    }
    public List<ReviewDetailResponse> getComboReviews(long id){
        List<ReviewDetail> reviewDetails = reviewDetailRepository.findByTypeAndBookingDetail_Combo_Id(ReviewDetailType.COMBO, id);
        return reviewDetailMapper.toReviewDetailResponseList(reviewDetails);
    }
    public List<ReviewDetailResponse> getServiceReviews(long id){
        List<ReviewDetail> reviewDetails = reviewDetailRepository.findByTypeAndBookingDetail_Service_Id(ReviewDetailType.SERVICE, id);
        return reviewDetailMapper.toReviewDetailResponseList(reviewDetails);
    }
}
