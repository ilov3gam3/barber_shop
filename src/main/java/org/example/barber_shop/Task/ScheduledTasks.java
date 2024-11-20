package org.example.barber_shop.Task;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Entity.Booking;
import org.example.barber_shop.Entity.Notification;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Repository.BookingRepository;
import org.example.barber_shop.Repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Scheduled(fixedRate = 60 * 1000) // every 1 min
    public void scheduledTaskMinutes() {
        System.out.println("min task run at " + LocalDateTime.now().getMinute());
        try {
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30); // Strip seconds
            Timestamp startTime = Timestamp.valueOf(now); // Start of the current minute
            Timestamp endTime = Timestamp.valueOf(now.plusMinutes(1).minusSeconds(1)); // End of the current minute
            System.out.println(startTime);
            System.out.println(endTime);
            List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(
                    BookingStatus.PAID, startTime, endTime);
            System.out.println(bookings.size());
            if (bookings.isEmpty()) return;

            List<Notification> notifications = new ArrayList<>();
            for (Booking booking : bookings) {
                Notification tempNotification = new Notification();
                tempNotification.setUser(booking.getCustomer());
                tempNotification.setType(NotificationType.UPCOMING_BOOKING);
                tempNotification.setTitle("Incoming booking");
                tempNotification.setMessage("You have an incoming booking, please prepare.");
                tempNotification.setTargetUrl("");
                tempNotification.setSeen(false);
                notifications.add(tempNotification);
            }
            notifications = notificationRepository.saveAll(notifications);

            for (int i = 0; i < bookings.size(); i++) {
                User user = bookings.get(i).getCustomer();
                Notification notification = notifications.get(i);
                notification.setUser(null);
                simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
                System.out.println("message send to " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error during scheduled task execution: " + e.getMessage());
        }
    }
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    public void notifyUnpaidBookings() {
        System.out.println("hour task run");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime = now.plusHours(24).truncatedTo(ChronoUnit.MINUTES); // 24 hours ahead
            Timestamp startTime = Timestamp.valueOf(targetTime);
            Timestamp endTime = Timestamp.valueOf(targetTime.plusMinutes(59).withSecond(59)); // One-hour range
            List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(
                    BookingStatus.CONFIRMED, startTime, endTime);
            System.out.println(bookings.size());
            if (bookings.isEmpty()) return;

            List<Notification> notifications = new ArrayList<>();
            for (Booking booking : bookings) {
                Notification tempNotification = new Notification();
                tempNotification.setUser(booking.getCustomer());
                tempNotification.setType(NotificationType.UNPAID_BOOKING_REMINDER);
                tempNotification.setTitle("Booking Reminder");
                tempNotification.setMessage("Your booking is scheduled for tomorrow. Please confirm or pay to avoid cancellation.");
                tempNotification.setTargetUrl(""); // Optionally, link to payment page
                tempNotification.setSeen(false);
                notifications.add(tempNotification);
            }
            notifications = notificationRepository.saveAll(notifications);

            for (Notification notification : notifications) {
                User user = notification.getUser();
                notification.setUser(null);
                simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
                System.out.println("message send to " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error during unpaid booking notification: " + e.getMessage());
        }
    }
}
