package org.example.barber_shop.DTO.Voucher;

import java.sql.Timestamp;
import java.time.LocalDate;

public class VoucherResponse {
    public long id;
    public String code;
    public int maxUses;
    public int discount;
    public int maxDiscount;
    public LocalDate startDate;
    public LocalDate endDate;
    public long minPrice;
    public int uses;
    public boolean disable;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
