package org.example.barber_shop.DTO.User;

import java.sql.Date;

public class UpdateUserRequest {
    public long id;
    public String name;
    public String email;
    public String phone;
    public Date dob;
    public String password;
    public String description;
    public boolean blocked;
}
