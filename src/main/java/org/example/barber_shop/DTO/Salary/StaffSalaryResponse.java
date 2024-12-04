package org.example.barber_shop.DTO.Salary;

import org.example.barber_shop.DTO.User.UserResponseNoFile;

import java.sql.Timestamp;

public class StaffSalaryResponse {
    public long id;
    public UserResponseNoFile staff;
    public double rate;
    public double percentage;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
