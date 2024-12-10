package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByDeletedFalse();
    Voucher findByIdAndDeletedFalse(long id);
    Voucher findByCodeAndDeletedFalse(String code);
    List<Voucher> findByDeletedFalseAndDisabledFalse();
}
