package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.StaffSalary;
import org.example.barber_shop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffSalaryRepository extends JpaRepository<StaffSalary, Long> {
    StaffSalary findByStaff(User staff);
}
