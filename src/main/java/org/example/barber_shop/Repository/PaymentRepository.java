package org.example.barber_shop.Repository;

import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.TransactionStatus;
import org.example.barber_shop.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderInfo(String orderInfo);
    @Query("SELECT DISTINCT p FROM Payment p " +
            "JOIN p.bookings b " +
            "WHERE b.customer.id = :id")
    List<Payment> findByCustomer_Id(long id);

    @Query("SELECT p FROM Payment p " +
            "JOIN p.bookings b " +
            "WHERE p.id = :paymentId AND b.customer.id = :customerId")
    Optional<Payment> findByIdAndCustomerId(@Param("paymentId") long paymentId, @Param("customerId") long customerId);

    @Query("SELECT DISTINCT p FROM Payment p " +
            "JOIN p.bookings b " +
            "WHERE b.customer.id = :id and p.transactionStatus = :status and p.voucherCode = :voucherCode")
    List<Payment> findByCustomer_IdAndStatusAndVoucherCode(long id, TransactionStatus status, String voucherCode);
}
