package org.example.barber_shop.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerTopBookings {
    private Long id;
    private Long amountUsed;
    private Long bookingCount;
}
