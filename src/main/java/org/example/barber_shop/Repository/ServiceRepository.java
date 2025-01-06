package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {
    List<Service> findByDeletedFalse();
    List<Service> findAllByIdInAndDeletedFalse(List<Long> ids);
}
