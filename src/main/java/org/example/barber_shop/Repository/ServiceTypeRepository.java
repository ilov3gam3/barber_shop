package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    List<ServiceType> findByDeletedFalse();
}
