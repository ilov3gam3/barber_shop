package org.example.barber_shop.DTO.Salary;

import org.example.barber_shop.DTO.User.UserResponseNoFile;

import java.time.LocalDate;

public class WeeklySalaryAdminResponse {
    public long id;
    private UserResponseNoFile staff;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private double salary;
    private double hours;
    private double percentage;
    private double totalMoneyMade;
    private double totalEarnings;
}
