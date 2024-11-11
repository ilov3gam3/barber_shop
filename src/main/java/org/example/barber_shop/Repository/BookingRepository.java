package org.example.barber_shop.Repository;


import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer_Id(Long customerId);
    List<Booking> findByStaff_IdAndStatus(Long staffId, BookingStatus status);
}
