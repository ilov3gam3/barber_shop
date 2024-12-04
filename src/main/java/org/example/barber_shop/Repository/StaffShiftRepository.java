package org.example.barber_shop.Repository;



import org.example.barber_shop.Entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {
    List<StaffShift> findByStaffIdAndDateBetween(Long staff_id, LocalDate date, LocalDate date2);
    List<StaffShift> findByDateBetween(LocalDate start, LocalDate end);
    List<StaffShift> findByStaffIdAndDateIn(Long staff_id, Collection<LocalDate> date);
    List<StaffShift> findByStaffIdAndDate(Long staff_id, LocalDate date);
}