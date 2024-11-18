package org.example.barber_shop.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "payment")
    private List<Booking> bookings;

    private long amount;
    public String txnRef;
    public String orderInfo;
    public String bankCode;
    public String transactionNo;
    public TransactionStatus transactionStatus;
    public String cardType;
    public String bankTranNo;
    public Timestamp paid_at;
}
