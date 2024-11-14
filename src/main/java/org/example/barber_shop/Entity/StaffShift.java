package org.example.barber_shop.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff_shifts")
@ToString
public class StaffShift extends DistributedEntity{
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;
    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
