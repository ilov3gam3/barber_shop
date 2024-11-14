package org.example.barber_shop.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "shifts")
@ToString
public class Shift extends DistributedEntity{
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
}
