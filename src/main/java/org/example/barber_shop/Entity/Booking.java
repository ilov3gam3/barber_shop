package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.barber_shop.Constants.BookingStatus;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bookings")
@ToString
public class Booking extends DistributedEntity{
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    private Timestamp startTime;
    private Timestamp endTime;

    @OneToMany(mappedBy = "booking")
    private List<BookingDetail> bookingDetails;

    private long totalPrice;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToOne(mappedBy = "booking")
    private Review review;
}
