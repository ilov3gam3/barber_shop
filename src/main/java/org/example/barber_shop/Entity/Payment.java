package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.barber_shop.Constants.TransactionStatus;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payments")
@ToString
public class Payment extends DistributedEntity{

    @OneToMany(mappedBy = "payment")
    private List<Booking> bookings;
    private long amount;
    public String txnRef;
    public String orderInfo;
    public String bankCode;
    public String transactionNo;
    @Enumerated(EnumType.STRING)
    public TransactionStatus transactionStatus;
    public String cardType;
    public String bankTranNo;
    public Timestamp paid_at;
    public String voucherCode;
    public int voucherDiscount;
    public long discountAmount;
}
