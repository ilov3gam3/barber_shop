package org.example.barber_shop.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffTopBookings {
    private Long id;
    private Long amountMade;
    private Long bookingCount;
}
