package org.example.barber_shop.Task;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Entity.Booking;
import org.example.barber_shop.Entity.Notification;
import org.example.barber_shop.Repository.BookingRepository;
import org.example.barber_shop.Repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    @Scheduled(fixedRate = 60 * 1000) // every 1 min
    public void scheduledTaskMinutes() {
        /*Timestamp currentTime = Timestamp.from(Instant.now());
        Timestamp oneMinutesBefore = Timestamp.from(Instant.now().minusSeconds(60)); // 30 minutes
        List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(BookingStatus.PAID, currentTime, oneMinutesBefore);
        List<Notification> notifications = new ArrayList<>();
        for (Booking booking : bookings) {
            Notification tempNotification = new Notification();
            tempNotification.setUser(booking.getCustomer());
            tempNotification.setType(NotificationType.UPCOMING_BOOKING);
            tempNotification.setTitle("Incoming booking");
            tempNotification.setMessage("You have a incoming booking, please prepare.");
            tempNotification.setTargetUrl("");
            tempNotification.setSeen(false);
            notifications.add(tempNotification);
        }
        notificationRepository.saveAll(notifications);*/
    }
}
