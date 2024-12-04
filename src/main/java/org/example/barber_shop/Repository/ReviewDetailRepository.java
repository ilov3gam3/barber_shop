package org.example.barber_shop.Repository;

import org.example.barber_shop.Constants.ReviewDetailType;
import org.example.barber_shop.Entity.Combo;
import org.example.barber_shop.Entity.ReviewDetail;
import org.example.barber_shop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReviewDetailRepository extends JpaRepository<ReviewDetail, Long> {
    ReviewDetail findByIdAndReview_Booking_Customer(Long id, User review_booking_customer);
    List<ReviewDetail> findByTypeAndBookingDetail_ComboIn(ReviewDetailType type, Collection<Combo> bookingDetail_combo);
    List<ReviewDetail> findByTypeAndBookingDetail_Combo(ReviewDetailType type, Combo bookingDetail_combo);
    List<ReviewDetail> findByTypeAndBookingDetail_Combo_Id(ReviewDetailType type, Long bookingDetail_combo_id);
    List<ReviewDetail> findByTypeAndBookingDetail_Service_Id(ReviewDetailType type, Long bookingDetail_service_id);
}
