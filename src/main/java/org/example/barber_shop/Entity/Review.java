package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "reviews")
@ToString
public class Review extends DistributedEntity{
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Min(1)
    @Max(5)
    private int staffRating;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String staffComment;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewDetail> details;
}
