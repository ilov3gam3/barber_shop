package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Shift;
import org.example.barber_shop.Entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {
    StaffShift findByDateAndShift(LocalDate date, Shift shift);
}
