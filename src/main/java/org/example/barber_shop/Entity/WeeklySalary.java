package org.example.barber_shop.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "weekly_salaries")
@ToString
public class WeeklySalary extends DistributedEntity{
    @ManyToOne
    private User staff;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private double salary;
    private double hours;
    private double percentage;
    private double totalMoneyMade;
    private double totalEarnings;

    public void calculateTotalEarnings() {
        this.totalEarnings = (percentage / 100) * totalMoneyMade + (salary * hours);
    }
}
