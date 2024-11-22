package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByDeletedFalse();
    List<Service> findAllByIdInAndDeletedFalse(List<Long> ids);
}
