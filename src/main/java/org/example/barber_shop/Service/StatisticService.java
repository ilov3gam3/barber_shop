package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.Combo.ComboTopBooking;
import org.example.barber_shop.DTO.Service.ServiceTopBookings;
import org.example.barber_shop.DTO.Statistic.YearResponse;
import org.example.barber_shop.DTO.User.CustomerTopBookings;
import org.example.barber_shop.DTO.User.StaffTopBookings;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Repository.BookingDetailRepository;
import org.example.barber_shop.Repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;

    public List<CustomerTopBookings> getTopCustomers(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return bookingRepository.findTopCustomers();
        } else if (from == null || to == null) {
            throw new LocalizedException("from.date.to.date.must.provided");
        } else {
            if (from.isAfter(to)) {
                throw new LocalizedException("from.date.must.before.to.date");
            }
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(23, 59, 59);
            return bookingRepository.findTopCustomers(start, end);
        }
    }

    public List<ServiceTopBookings> getTopServices(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return bookingDetailRepository.findTopServices();
        } else if (from == null || to == null) {
            throw new LocalizedException("from.date.to.date.must.provided");
        } else {
            if (from.isAfter(to)) {
                throw new LocalizedException("from.date.must.before.to.date");
            }
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(23, 59, 59);
            return bookingDetailRepository.findTopServices(start, end);
        }
    }

    public List<ComboTopBooking> getTopCombos(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return bookingDetailRepository.getTopCombos();
        } else if (from == null || to == null) {
            throw new LocalizedException("from.date.to.date.must.provided");
        } else {
            if (from.isAfter(to)) {
                throw new LocalizedException("from.date.must.before.to.date");
            }
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(23, 59, 59);
            return bookingDetailRepository.getTopCombos(start, end);
        }
    }
    public YearResponse getYearStatistic(Integer year){
        if (year == null){
            LocalDate today = LocalDate.now();
            year = today.getYear();
        }
        List<Object[]> rawResponse = bookingRepository.getMonthlyStatistics(year);
        Map<Month, YearResponse.Booking> monthlyData = new HashMap<>();
        for (Object[] record : rawResponse) {
            Integer monthNumber = (Integer) record[0];
            Integer bookingCount = ((Number) record[1]).intValue();
            Long totalAmount = ((Number) record[2]).longValue();
            Month month = Month.of(monthNumber);
            monthlyData.put(month, new YearResponse.Booking(bookingCount, totalAmount));
        }
        return new YearResponse(year, monthlyData);
    }
    public List<StaffTopBookings> getTopStaffs(LocalDate from, LocalDate to){
        if (from == null && to == null) {
            return bookingRepository.findTopStaffs();
        } else if (from == null || to == null) {
            throw new LocalizedException("from.date.to.date.must.provided");
        } else {
            if (from.isAfter(to)) {
                throw new LocalizedException("from.date.must.before.to.date");
            }
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(23, 59, 59);
            System.out.println(start);
            System.out.println(end);
            return bookingRepository.findTopStaffs(start, end);
        }
    }
}
