package org.example.barber_shop.DTO.User;

import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.File.FileResponseNoOwner;

import java.sql.Timestamp;
import java.util.Date;

public class UserResponse {
    public int id;
    public String name;
    public String email;
    public String phone;
    public Date dob;
    public FileResponseNoOwner avatar;
    public boolean verified;
    public boolean blocked;
    public Role role;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
