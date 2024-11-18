package org.example.barber_shop.Repository;


import org.example.barber_shop.Entity.Shift;
import org.example.barber_shop.Entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
 import java.util.List;

public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {
    StaffShift findByDateAndShift(LocalDate date, Shift shift);
    List<StaffShift> findByStaffIdAndDateBetween(Long staff_id, LocalDate date, LocalDate date2);
    List<StaffShift> findByDateBetween(LocalDate start, LocalDate end);
    List<StaffShift> findByStaffIdAndDate(Long staff_id, LocalDate date);
//    List<StaffShift> findByStaffAndDate(Long staff_id, LocalDate date);
}