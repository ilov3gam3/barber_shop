package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "booking_details")
@ToString
public class BookingDetail extends DistributedEntity{
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    private Service service;

    @ManyToOne
    private Combo combo;

    private long finalPrice;

    public BookingDetail(Booking booking, Service service) {
        this.booking = booking;
        this.service = service;
    }

    public BookingDetail(Booking booking, Combo combo) {
        this.booking = booking;
        this.combo = combo;
    }
}
