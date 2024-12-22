package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Constants.Rank;
import org.example.barber_shop.Entity.Notification;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Repository.BookingRepository;
import org.example.barber_shop.Repository.NotificationRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class RankService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Value("${front_end_server}")
    private String fe_server;
    @Async
    public void checkRank(long customer_id){
        User user = userRepository.findById(customer_id).orElse(null);
        if(user == null){
            return;
        }
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
        if (checkUserRankedUpInMonth(user.getId(), startMonth, endMonth)){
            return;
        }
        long amountUsed = bookingRepository.sumTotalPrice(customer_id, startMonth, endMonth);
        switch (user.getRank()){
            case BRONZE -> {
                if (amountUsed >= 1000000){
                    user.setRank(Rank.SILVER);
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setType(NotificationType.GENERAL_INFO);
                    notification.setTitle("You have ranked up");
                    notification.setMessage("You have ranked up to " + Rank.SILVER + ", from now on u can use vouchers for rank " + Rank.SILVER + ". but keep in mind that you have to use 200000 vnd per month to keep your rank.");
                    notification.setTargetUrl(fe_server + "/#");
                    notification.setSeen(false);
                    notification = notificationRepository.save(notification);
                    notification.setUser(null);
                    simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
                }
            }
            case SILVER -> {
                if (amountUsed >= 5000000){
                    user.setRank(Rank.GOLD);
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setType(NotificationType.GENERAL_INFO);
                    notification.setTitle("You have ranked up");
                    notification.setMessage("You have ranked up to " + Rank.GOLD + ", from now on u can use vouchers for rank " + Rank.GOLD + ". but keep in mind that you have to use 500000 vnd per month to keep your rank.");
                    notification.setTargetUrl(fe_server + "/#");
                    notification.setSeen(false);
                    notification = notificationRepository.save(notification);
                    notification.setUser(null);
                    simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
                }
            }
            case GOLD -> {
                if (amountUsed >= 10000000){
                    user.setRank(Rank.DIAMOND);
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setType(NotificationType.GENERAL_INFO);
                    notification.setTitle("You have ranked up");
                    notification.setMessage("You have ranked up to " + Rank.DIAMOND + ", from now on u can use vouchers for rank " + Rank.DIAMOND + ". but keep in mind that you have to use 1000000 vnd per month to keep your rank.");
                    notification.setTargetUrl(fe_server + "/#");
                    notification.setSeen(false);
                    notification = notificationRepository.save(notification);
                    notification.setUser(null);
                    simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
                }
            }
        }
        userRepository.save(user);
    }
    public boolean checkUserRankedUpInMonth(long user_id, Timestamp startMonth, Timestamp endMonth){
        Notification notification = notificationRepository.findByUser_IdAndTypeAndTitleAndCreatedAtGreaterThanAndCreatedAtLessThan(user_id, NotificationType.GENERAL_INFO, "You have ranked up", startMonth, endMonth);
        return notification != null;
    }
}
