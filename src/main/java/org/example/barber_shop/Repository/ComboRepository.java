package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComboRepository extends JpaRepository<Combo, Long> {
    List<Combo> findByDeletedFalse();
}
