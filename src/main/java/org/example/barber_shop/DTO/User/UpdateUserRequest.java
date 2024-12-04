package org.example.barber_shop.DTO.User;


import org.example.barber_shop.Constants.Role;

import java.sql.Date;

public class UpdateUserRequest {
    public long id;
    public String name;
    public String email;
    public String phone;
    public Date dob;
    public String password;
    public Role role;
    public boolean blocked;
}
