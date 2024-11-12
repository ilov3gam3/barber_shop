package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderInfo(String orderInfo);
}
