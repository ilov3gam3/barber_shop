package org.example.barber_shop.Repository;

import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Entity.Notification;
import org.example.barber_shop.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Notification findByIdAndUser_Id(Long id, long user_id);
    Notification findByUser_IdAndTypeAndTitleAndCreatedAtGreaterThanAndCreatedAtLessThan(Long user_id, NotificationType type, String title, Timestamp startMonth, Timestamp endMonth);
}
