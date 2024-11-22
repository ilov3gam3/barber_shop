package org.example.barber_shop.Constants;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("Booking is created but not yet paid."),
    PAID("Payment is completed, but the appointment may still be pending."),
    //    CONFIRMED("Booking is confirmed, awaiting the appointment date and time."),
    CANCELLED("Booking is canceled by the customer or barber."),
    //    RESCHEDULED("Appointment date/time has been changed."),
    NO_SHOW("Customer did not show up for the appointment."),
    //    IN_PROGRESS("Service is currently being provided."),
    COMPLETED("Appointment is finished.");
//    REFUNDED("Payment was refunded, often related to a canceled booking.");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

}
