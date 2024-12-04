package org.example.barber_shop.DTO.Salary;

import java.time.LocalDate;

public class WeeklySalaryStaffResponse {
    public long id;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private double salary;
    private double hours;
    private double percentage;
    private double totalMoneyMade;
    private double totalEarnings;
}
