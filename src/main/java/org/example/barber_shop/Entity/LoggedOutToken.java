package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "logged_out_tokens")
@ToString
public class LoggedOutToken extends DistributedEntity{
    @Column(nullable = false, length = 1000)
    String token;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    Timestamp expTime;
}
