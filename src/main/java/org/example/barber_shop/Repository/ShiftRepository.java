package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
