package org.example.barber_shop.DTO.User;

import org.example.barber_shop.Constants.Role;

import java.sql.Date;


public class AdminCreateUser {
    public String name;
    public String email;
    public String phone;
    public Date dob;
    public String password;
    public String description;
    public Role role;
}
