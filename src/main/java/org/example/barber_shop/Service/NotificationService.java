package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Entity.Notification;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Repository.NotificationRepository;
import org.example.barber_shop.Util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Page<Notification> getAllNotifications(int page, int size) {
        User user = SecurityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationResponses = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        for (int i = 0; i < notificationResponses.getContent().size(); i++) {
            notificationResponses.getContent().get(i).setUser(null);
        }
        return notificationResponses;
    }
    public Notification markSeen(long id){
        User user = SecurityUtils.getCurrentUser();
        Notification notification = notificationRepository.findByIdAndUser_Id(id, user.getId());
        if(notification != null){
            notification.setSeen(true);
            notification = notificationRepository.save(notification);
            notification.setUser(null);
            return notification;
        } else {
            throw new RuntimeException("Notification not found with id " + id);
        }
    }
}
