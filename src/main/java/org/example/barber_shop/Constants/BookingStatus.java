package org.example.barber_shop.Constants;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("Booking is created but not yet paid."),
    REJECTED("Staff rejected your booking."),
    PAID("Payment is completed, but the appointment may still be pending."),
    CANCELLED("Booking is canceled by the customer or barber."),
    NO_SHOW("Customer did not show up for the appointment."),
    COMPLETED("Appointment is finished.");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

}
