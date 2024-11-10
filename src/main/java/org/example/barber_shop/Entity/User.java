package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.barber_shop.Constants.Role;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@ToString
public class User extends DistributedEntity{
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;
    private Date dob;
    private String phone;
    @ManyToOne
    @JoinColumn(name = "avatar_id")
    private File avatar;
    private String password;
    private String token;
    private String email;
    private boolean isVerified;
    private boolean isBlocked;

    @Enumerated(EnumType.STRING)
    private Role role;
}
