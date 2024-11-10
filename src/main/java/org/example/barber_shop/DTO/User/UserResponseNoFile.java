package org.example.barber_shop.DTO.User;

import org.example.barber_shop.Constants.Role;

import java.sql.Timestamp;
import java.util.Date;

public class UserResponseNoFile {
    public int id;
    public String name;
    public String email;
    public String phone;
    public Date dob;
    public boolean verified;
    public boolean blocked;
    public Role role;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
