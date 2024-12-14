package org.example.barber_shop.Task;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Constants.Rank;
import org.example.barber_shop.Constants.Role;
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
import java.time.temporal.TemporalAdjusters;
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
    private final UserRepository userRepository;
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
            // cancel booking if you don't show up in 15min
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(15);
            startTime = Timestamp.valueOf(now);
            endTime = Timestamp.valueOf(now.plusMinutes(1).minusSeconds(1));
            bookings = bookingRepository.findByStatusAndStartTimeBetween(BookingStatus.PENDING, startTime, endTime);
            notifications = new ArrayList<>();
            for (int i = 0; i < bookings.size(); i++) {
                bookings.get(i).setStatus(BookingStatus.CANCELLED);
                Notification tempNotification = new Notification();
                tempNotification.setUser(bookings.get(i).getCustomer());
                tempNotification.setType(NotificationType.BOOKING_CANCELLATION);
                tempNotification.setTitle("Booking cancelled");
                tempNotification.setMessage("Your booking is canceled, because you dont show up 15 minutes before start time.");
                tempNotification.setTargetUrl(fe_server + "/temp/booking/" + bookings.get(i).getId());
                tempNotification.setSeen(false);
                notifications.add(tempNotification);
            }
            bookingRepository.saveAll(bookings);
            notificationRepository.saveAll(notifications);
            for (int i = 0; i < bookings.size(); i++) {
                User user = bookings.get(i).getCustomer();
                Notification notification = notifications.get(i);
                notification.setUser(null);
                simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
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
    @Scheduled(cron = "0 0 0 * * MON")
    public void generateWeeklySalary(){
        LocalDateTime startDateWeek = TimeUtil.getLastWeekStartDate();
        LocalDateTime endDateWeek = TimeUtil.getLastWeekEndDate();
        List<Booking> bookings = bookingRepository.findByStatusAndStartTimeBetween(BookingStatus.PAID, Timestamp.valueOf(startDateWeek), Timestamp.valueOf(endDateWeek));
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

    @Scheduled(cron = "0 0 0 21 * ?")
    public void announceRank(){
        List<User> customers = userRepository.findAllByRole(Role.ROLE_CUSTOMER);
        LocalDateTime startOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
        Timestamp startMonth = Timestamp.valueOf(startOfMonth);
        Timestamp endMonth = Timestamp.valueOf(endOfMonth);
        for (User customer : customers) {
            long amountUsed = bookingRepository.sumTotalPrice(customer.getId(), startMonth, endMonth);
            switch (customer.getRank()){
                case SILVER -> {
                    if (amountUsed < 200000){
                        Notification notification = new Notification();
                        notification.setUser(customer);
                        notification.setType(NotificationType.GENERAL_INFO);
                        notification.setTitle("Use our services to keep your rank");
                        notification.setMessage("You need to use " + (200000 - amountUsed) + " to keep you stay at rank SILVER.");
                        notification.setTargetUrl(fe_server + "/services");
                        notification.setSeen(false);
                        notificationRepository.save(notification);
                        notification.setUser(null);
                        simpMessagingTemplate.convertAndSendToUser(customer.getEmail(), "/topic", notification);
                    }
                }
                case GOLD -> {
                    if (amountUsed < 300000){
                        Notification notification = new Notification();
                        notification.setUser(customer);
                        notification.setType(NotificationType.GENERAL_INFO);
                        notification.setTitle("Use our services to keep your rank");
                        notification.setMessage("You need to use " + (300000 - amountUsed) + " to keep you stay at rank GOLD.");
                        notification.setTargetUrl(fe_server + "/services");
                        notification.setSeen(false);
                        notificationRepository.save(notification);
                        notification.setUser(null);
                        simpMessagingTemplate.convertAndSendToUser(customer.getEmail(), "/topic", notification);
                    }
                }
                case DIAMOND -> {
                    if (amountUsed < 500000){
                        Notification notification = new Notification();
                        notification.setUser(customer);
                        notification.setType(NotificationType.GENERAL_INFO);
                        notification.setTitle("Use our services to keep your rank");
                        notification.setMessage("You need to use " + (500000 - amountUsed) + " to keep you stay at rank DIAMOND.");
                        notification.setTargetUrl(fe_server + "/services");
                        notification.setSeen(false);
                        notificationRepository.save(notification);
                        notification.setUser(null);
                        simpMessagingTemplate.convertAndSendToUser(customer.getEmail(), "/topic", notification);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "1 0 0 1 * ?")
    public void checkRank() {
        List<User> customers = userRepository.findAllByRole(Role.ROLE_CUSTOMER);
        LocalDateTime startOfLastMonth = LocalDateTime.now()
                .minusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime endOfLastMonth = LocalDateTime.now()
                .minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
        Timestamp startMonth = Timestamp.valueOf(startOfLastMonth);
        Timestamp endMonth = Timestamp.valueOf(endOfLastMonth);
        for (User customer : customers) {
            long amountUsed = bookingRepository.sumTotalPrice(customer.getId(), startMonth, endMonth);
            switch (customer.getRank()){
                case SILVER -> {
                    if (amountUsed < 200000){
                        customer.setRank(Rank.BRONZE);
                        userRepository.save(customer);
                        Notification notification = new Notification();
                        notification.setUser(customer);
                        notification.setType(NotificationType.GENERAL_INFO);
                        notification.setTitle("You are back to rank " + Rank.BRONZE);
                        notification.setMessage("You only use " + amountUsed + " not enough to keep you at rank " + Rank.SILVER + ".");
                        notification.setTargetUrl(fe_server + "/");
                        notification.setSeen(false);
                        notificationRepository.save(notification);
                        notification.setUser(null);
                        simpMessagingTemplate.convertAndSendToUser(customer.getEmail(), "/topic", notification);
                    }
                }
                case GOLD -> {
                    if (amountUsed < 300000){
                        customer.setRank(Rank.BRONZE);
                        userRepository.save(customer);
                        Notification notification = new Notification();
                        notification.setUser(customer);
                        notification.setType(NotificationType.GENERAL_INFO);
                        notification.setTitle("You are back to rank " + Rank.BRONZE);
                        notification.setMessage("You only use " + amountUsed + " not enough to keep you at rank " + Rank.GOLD + ".");
                        notification.setTargetUrl(fe_server + "/");
                        notification.setSeen(false);
                        notificationRepository.save(notification);
                        notification.setUser(null);
                        simpMessagingTemplate.convertAndSendToUser(customer.getEmail(), "/topic", notification);
                    }
                }
                case DIAMOND -> {
                    if (amountUsed < 500000){
                        customer.setRank(Rank.BRONZE);
                        userRepository.save(customer);
                        Notification notification = new Notification();
                        notification.setUser(customer);
                        notification.setType(NotificationType.GENERAL_INFO);
                        notification.setTitle("You are back to rank " + Rank.BRONZE);
                        notification.setMessage("You only use " + amountUsed + " not enough to keep you at rank " + Rank.DIAMOND + ".");
                        notification.setTargetUrl(fe_server + "/");
                        notification.setSeen(false);
                        notificationRepository.save(notification);
                        notification.setUser(null);
                        simpMessagingTemplate.convertAndSendToUser(customer.getEmail(), "/topic", notification);
                    }
                }
            }
        }
    }
}
