package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.barber_shop.Constants.Rank;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vouchers")
@ToString
public class Voucher extends DistributedEntity{
    @Column(unique = true)
    private String code;
    private int maxUses;
    private int discount;
    private int maxDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private long minPrice;
    private int uses;
    @Enumerated(EnumType.STRING)
    private Rank forRank = Rank.BRONZE;
    private boolean disabled;
    private boolean deleted = false;

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return !disabled && (startDate == null || !today.isBefore(startDate))
                && (endDate == null || !today.isAfter(endDate));
    }

    public boolean canUse() {
        return (maxUses == 0 || uses < maxUses);
    }
    public long calculateDiscount(long price) {
        long applicableDiscount = (price * discount) / 100;
        return Math.min(applicableDiscount, maxDiscount);
    }
    public boolean isValid(Timestamp input) {
        if (input == null) {
            return false;
        }
        LocalDateTime dateTime = input.toLocalDateTime();

        return !disabled &&
                (startDate == null || !dateTime.isBefore(startDate.atStartOfDay())) &&
                (endDate == null || !dateTime.isAfter(endDate.atStartOfDay()));
    }
}
