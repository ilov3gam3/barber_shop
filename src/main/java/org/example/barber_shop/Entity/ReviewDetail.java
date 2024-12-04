package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.example.barber_shop.Constants.ReviewDetailType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "review_details")
@ToString
public class ReviewDetail extends DistributedEntity{
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "booking_detail_id", nullable = false)
    private BookingDetail bookingDetail;

    @Min(1)
    @Max(5)
    private int rating;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String comment;

    @Enumerated(EnumType.STRING)
    private ReviewDetailType type;
}
