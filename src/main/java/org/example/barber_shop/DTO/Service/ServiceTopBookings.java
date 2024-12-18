package org.example.barber_shop.DTO.Service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceTopBookings {
    private Long id;
    private Long bookingCount;
    private Long amountUsed;
}
