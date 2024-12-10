package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.Rank;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Repository.BookingRepository;
import org.example.barber_shop.Repository.UserRepository;
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
        long amountUsed = bookingRepository.sumTotalPrice(customer_id, startMonth, endMonth);
        switch (user.getRank()){
            case BRONZE -> {
                if (amountUsed >= 1000000){
                    user.setRank(Rank.SILVER);
                }
            }
            case SILVER -> {
                if (amountUsed >= 5000000){
                    user.setRank(Rank.GOLD);
                }
            }
            case GOLD -> {
                if (amountUsed >= 10000000){
                    user.setRank(Rank.DIAMOND);
                }
            }
        }
        userRepository.save(user);
    }
}
