package org.example.barber_shop.DTO.Combo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComboTopBooking {
    private Long id;
    private Long amountUsed;
    private Long bookingCount;
}
