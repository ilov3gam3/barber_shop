package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.barber_shop.Constants.NotificationType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
@ToString
public class Notification extends DistributedEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String title;
    private String message;
    private String targetUrl;
    private boolean isSeen;
}