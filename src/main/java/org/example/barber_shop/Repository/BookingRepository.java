package org.example.barber_shop.Repository;


import org.example.barber_shop.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
