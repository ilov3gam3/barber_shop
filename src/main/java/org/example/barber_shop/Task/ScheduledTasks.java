package org.example.barber_shop.Task;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Service.MailService;
import org.example.barber_shop.Util.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MailService mailService;
    private final StaffShiftRepository staffShiftRepository;
    private final StaffSalaryRepository staffSalaryRepository;
    private final WeeklySalaryRepository weeklySalaryRepository;
    @Value("${front_end_server}")
    private String fe_server;
    @Scheduled(fixedRate = 60 * 1000) // every 1 min
    public void scheduledTaskMinutes() {
        try {
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30); // Strip seconds
            Timestamp startTime = Timestamp.valueOf(now); // Start of the current minute
            Timestamp endTime = Timestamp.valueOf(now.plusMinutes(1).minusSeconds(1)); // End of the current minute
            List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(
                    BookingStatus.PAID, startTime, endTime);
            if (bookings.isEmpty()) return;

            List<Notification> notifications = new ArrayList<>();
            for (Booking booking : bookings) {
                Notification tempNotification = new Notification();
                tempNotification.setUser(booking.getCustomer());
                tempNotification.setType(NotificationType.UPCOMING_BOOKING);
                tempNotification.setTitle("Incoming booking");
                tempNotification.setMessage("You have an incoming booking, please prepare.");
                tempNotification.setTargetUrl(fe_server + "/temp/booking/" + booking.getId());
                tempNotification.setSeen(false);
                notifications.add(tempNotification);
            }
            notifications = notificationRepository.saveAll(notifications);

            for (int i = 0; i < bookings.size(); i++) {
                User user = bookings.get(i).getCustomer();
                Notification notification = notifications.get(i);
                notification.setUser(null);
                simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
                mailService.sendMailIncomingBooking(bookings.get(i).getCustomer().getEmail(), notification.getTargetUrl());
            }
        } catch (Exception e) {
            System.err.println("Error during scheduled task execution: " + e.getMessage());
        }
    }
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    public void notifyUnpaidBookings() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime = now.plusHours(24).truncatedTo(ChronoUnit.MINUTES); // 24 hours ahead
            Timestamp startTime = Timestamp.valueOf(targetTime);
            Timestamp endTime = Timestamp.valueOf(targetTime.plusMinutes(59).withSecond(59)); // One-hour range
            List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(
                    BookingStatus.PENDING, startTime, endTime);
            if (bookings.isEmpty()) return;

            List<Notification> notifications = new ArrayList<>();
            for (Booking booking : bookings) {
                Notification tempNotification = new Notification();
                tempNotification.setUser(booking.getCustomer());
                tempNotification.setType(NotificationType.UNPAID_BOOKING_REMINDER);
                tempNotification.setTitle("Booking Reminder");
                tempNotification.setMessage("Your booking is scheduled for tomorrow. Please confirm or pay to avoid cancellation.");
                tempNotification.setTargetUrl(fe_server + "/temp/booking/" + booking.getId());
                tempNotification.setSeen(false);
                notifications.add(tempNotification);
            }
            notifications = notificationRepository.saveAll(notifications);

            for (int i = 0; i < bookings.size(); i++) {
                User user = notifications.get(i).getUser();
                notifications.get(i).setUser(null);
                simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notifications.get(i));
                mailService.sendMailUnPaidBooking(bookings.get(i).getCustomer().getEmail(), notifications.get(i).getTargetUrl());
            }
        } catch (Exception e) {
            System.err.println("Error during unpaid booking notification: " + e.getMessage());
        }
    }
    @Scheduled(cron = "0 38 23 * * WED")
    public void generateWeeklySalary(){
        System.out.println("task tính lương chạy");
        LocalDateTime startDateWeek = TimeUtil.getLastWeekStartDate();
        LocalDateTime endDateWeek = TimeUtil.getLastWeekEndDate();
        List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(BookingStatus.PAID, Timestamp.valueOf(startDateWeek), Timestamp.valueOf(endDateWeek));
        System.out.println(bookings.size());
        List<WeeklySalary> weeklySalaries = new ArrayList<>();
        for (int i = 0; i < bookings.size(); i++) {
            User staff = bookings.get(i).getStaff();
            if (checkIfUserExistsInWeekSalaries(weeklySalaries, staff) == -1){
                WeeklySalary weeklySalary = new WeeklySalary();
                weeklySalary.setStaff(staff);
                weeklySalary.setWeekStartDate(startDateWeek.toLocalDate());
                weeklySalary.setWeekEndDate(endDateWeek.toLocalDate());
                setSalaryHourPercentage(weeklySalary, staff);
                weeklySalary.setTotalMoneyMade(bookings.get(i).getTotalPrice());
                weeklySalaries.add(weeklySalary);
            } else {
                weeklySalaries.get(i).setTotalMoneyMade(bookings.get(i).getTotalPrice() + weeklySalaries.get(i).getTotalMoneyMade());
            }
        }
        for (WeeklySalary weeklySalary : weeklySalaries) {
            weeklySalary.calculateTotalEarnings();
        }
        System.out.println(weeklySalaries);
        weeklySalaryRepository.saveAll(weeklySalaries);
    }
    public int checkIfUserExistsInWeekSalaries(List<WeeklySalary> weeklySalaries, User user) {
        for (int i = 0; i < weeklySalaries.size(); i++) {
            if (Objects.equals(weeklySalaries.get(i).getStaff().getId(), user.getId())) {
                return i;
            }
        }
        return -1;
    }
    public void setSalaryHourPercentage(WeeklySalary weeklySalary, User user) {
        List<StaffShift> staffShifts = staffShiftRepository.findByStaffIdAndDateBetween(user.getId(), weeklySalary.getWeekStartDate(), weeklySalary.getWeekEndDate());
        double hours = 0;
        for (StaffShift staffShift : staffShifts) {
            hours += calculateHours(staffShift.getStartTime(), staffShift.getEndTime());
        }
        weeklySalary.setHours(hours);
        System.out.println(user.getId());
        StaffSalary staffSalary = staffSalaryRepository.findByStaff(user);
        weeklySalary.setSalary(staffSalary.getRate());
        weeklySalary.setPercentage(staffSalary.getPercentage());
    }
    public static double calculateHours(LocalTime start, LocalTime end) {
        if (end.isBefore(start)) {
            end = end.plusHours(24);
        }
        Duration duration = Duration.between(start, end);
        return duration.toMinutes() / 60.0;
    }
}
