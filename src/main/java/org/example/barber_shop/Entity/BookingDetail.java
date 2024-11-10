package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    private int duration;
}
