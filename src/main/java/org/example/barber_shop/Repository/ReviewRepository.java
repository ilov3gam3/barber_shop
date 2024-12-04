package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Review;
import org.example.barber_shop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByIdAndBooking_Customer(Long id, User booking_customer);
    List<Review> findByBooking_Staff_Id(Long id);
}
