package org.example.barber_shop.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff_salaries")
@ToString
public class StaffSalary extends DistributedEntity{
    @OneToOne
    private User staff;
    @Min(0)
    private double rate;
    @Min(0)
    @Max(100)
    private double percentage;
}
