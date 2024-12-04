package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.WeeklySalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WeeklySalaryRepository extends JpaRepository<WeeklySalary, Long> {
    List<WeeklySalary> findByWeekStartDateAndWeekEndDate(LocalDate weekStartDate, LocalDate weekEndDate);
    List<WeeklySalary> findByStaff_IdAndWeekStartDateAndWeekEndDate(Long staffId, LocalDate weekStartDate, LocalDate weekEndDate);
}
