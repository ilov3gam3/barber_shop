package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {

}
