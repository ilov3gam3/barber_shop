package org.example.barber_shop.DTO.Voucher;

import java.time.LocalDate;

public class VoucherUpdateRequest {
    public long id;
    public String code;
    public int maxUses;
    public int discount;
    public int maxDiscount;
    public LocalDate startDate;
    public LocalDate endDate;
    public long minPrice;
    public boolean disabled;
}
