package org.example.barber_shop.Repository;


import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer_Id(Long customerId);
    List<Booking> findByStaff_IdAndStatus(Long staffId, BookingStatus status);
    List<Booking> findByStaff_IdAndStartTimeBetweenAndStatus(Long staff_id, Timestamp startTime, Timestamp endTime, BookingStatus status);
    List<Booking> findAllByIdInAndCustomer_IdAndStatus(List<Long> id, Long customerId, BookingStatus status);
    List<Booking> findByIdIn(List<Long> id);
}
