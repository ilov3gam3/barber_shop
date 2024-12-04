package org.example.barber_shop.DTO.Payment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentRequest {
    public List<Long> bookingIds;
    public String bankCode;
    public String language;
    public String voucherCode;
}
