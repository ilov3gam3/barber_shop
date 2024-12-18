package org.example.barber_shop.Repository;

import org.example.barber_shop.DTO.Combo.ComboTopBooking;
import org.example.barber_shop.DTO.Service.ServiceTopBookings;
import org.example.barber_shop.Entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
    @Query("select new org.example.barber_shop.DTO.Service.ServiceTopBookings(bd.service.id, count(bd.id), sum(bd.finalPrice)) from BookingDetail bd where (bd.booking.status = 'COMPLETED' or bd.booking.status = 'PAID') and bd.service.id is not null group by bd.service.id")
    List<ServiceTopBookings> findTopServices();
    @Query("select new org.example.barber_shop.DTO.Service.ServiceTopBookings(bd.service.id, count(bd.id), sum(bd.finalPrice)) from BookingDetail bd where (bd.booking.status = 'COMPLETED' or bd.booking.status = 'PAID') and bd.service.id is not null and bd.booking.startTime >= :from and bd.booking.endTime <= :to group by bd.service.id")
    List<ServiceTopBookings> findTopServices(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select new org.example.barber_shop.DTO.Combo.ComboTopBooking(bd.combo.id, sum(bd.finalPrice), count(bd.id)) from BookingDetail bd where (bd.booking.status = 'COMPLETED' or bd.booking.status = 'PAID') and bd.combo.id is not null group by bd.combo.id")
    List<ComboTopBooking> getTopCombos();
    @Query("select new org.example.barber_shop.DTO.Combo.ComboTopBooking(bd.combo.id, sum(bd.finalPrice), count(bd.id)) from BookingDetail bd where (bd.booking.status = 'COMPLETED' or bd.booking.status = 'PAID') and bd.combo.id is not null and bd.booking.startTime >= :from and bd.booking.endTime <= :to group by bd.combo.id")
    List<ComboTopBooking> getTopCombos(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
