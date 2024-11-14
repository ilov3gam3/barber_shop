package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.DTO.Booking.BookingResponseNoStaff;
import org.example.barber_shop.DTO.Booking.BookingResponseNoUser;
import org.example.barber_shop.DTO.Booking.WorkScheduleResponse;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Mapper.BookingMapper;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.DTO.Booking.BookingRequest;
import org.example.barber_shop.Exception.UserNotFoundException;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Util.TimeUtil;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final ServiceRepository serviceRepository;
    private final ComboRepository comboRepository;
    private final BookingMapper bookingMapper;

    public boolean isTimeValid(User staff, Timestamp startTime, Timestamp endTime) {
        List<Booking> bookings = bookingRepository.findByStaff_IdAndStatusAndStartTimeBeforeAndEndTimeAfter(staff.getId(), BookingStatus.CONFIRMED, endTime, startTime);
        if (bookings.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public BookingResponseNoUser addBooking(BookingRequest bookingRequest) {
        Optional<User> staff = userRepository.findById(bookingRequest.staff_id);
        if (staff.isPresent()) {
            User staff_checked = staff.get();

            List<Service> services = serviceRepository.findAllById(bookingRequest.serviceIds);
            List<Combo> combos = comboRepository.findAllById(bookingRequest.comboIds);
            int tempTime = 0;
            for (Service service : services) {
                tempTime += service.getEstimateTime();
            }
            for (Combo combo : combos) {
                tempTime += combo.getEstimateTime();
            }
            Timestamp endTime = TimeUtil.calculateEndTime(bookingRequest.startTime, tempTime);
            if (isTimeValid(staff_checked, bookingRequest.startTime, endTime)) {
                User customer = SecurityUtils.getCurrentUser();
                Booking booking = new Booking();
                booking.setStatus(BookingStatus.PENDING);
                booking.setCustomer(customer);
                booking.setStaff(staff_checked);
                booking.setNote(bookingRequest.note);
                booking.setStartTime(bookingRequest.startTime);
                Booking savedBooking = bookingRepository.save(booking);
                booking.setEndTime(endTime);
                List<BookingDetail> bookingDetails = new ArrayList<>();
                for (Service service : services) {
                    bookingDetails.add(new BookingDetail(savedBooking, service));
                }
                for (Combo combo : combos) {
                    bookingDetails.add(new BookingDetail(savedBooking, combo));
                }
                List<BookingDetail> savedBookingDetails = bookingDetailRepository.saveAll(bookingDetails);
                booking.setBookingDetails(savedBookingDetails);
                return bookingMapper.toResponse(booking);
            } else {
                throw new RuntimeException("Conflict with staff's time.");
            }
        } else {
            throw new UserNotFoundException("Staff not found.");
        }
    }
    /*public BookingResponseNoUser adminAddBooking(BookingRequest bookingRequest){

    }*/
    public List<BookingResponseNoUser> getBookingsOfCustomers(){
        long userId = SecurityUtils.getCurrentUserId();
        List<Booking> bookings = bookingRepository.findByCustomer_Id(userId);
        return bookingMapper.toResponses(bookings);
    }
    public BookingResponseNoUser confirmBooking(long booking_id){
        long staff_id = SecurityUtils.getCurrentUserId();
        Booking booking = bookingRepository.findByIdAndStatusAndStaff_Id(booking_id, BookingStatus.PENDING, staff_id);
        System.out.println(booking == null);
        if (booking != null) {
            List<Booking> confirmedBookingsOfAStaff = bookingRepository.findByStaff_IdAndStatusAndStartTimeBeforeAndEndTimeAfter(staff_id, BookingStatus.CONFIRMED, booking.getEndTime(), booking.getStartTime());
            System.out.println(confirmedBookingsOfAStaff.size());
            if (confirmedBookingsOfAStaff.isEmpty()) {
                booking.setStatus(BookingStatus.CONFIRMED);
                booking = bookingRepository.save(booking);
                return bookingMapper.toResponse(booking);
            } else {
                throw new RuntimeException("You already has a confirmed booking in this time.");
            }
        } else {
            throw new RuntimeException("Booking not found, or it's not on pending status.");
        }
    }

    public LocalDate[] getStartAndEndOfWeek(int week, int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startDate = startOfYear
                .with(weekFields.weekOfYear(), week)
                .with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));

        LocalDate endDate = startDate.plusDays(6);
        return new LocalDate[]{startDate, endDate};
    }

    public List<WorkScheduleResponse> getStaffWorkScheduleInWeek(Integer week, Integer year, long staff_id){
        if (week == null && year == null) {
            LocalDate today = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            week = today.get(weekFields.weekOfYear());
            year = today.getYear();
        } else if (week == null || year == null) {
            throw new RuntimeException("Both week and year must be provided or neither.");
        }
        LocalDate[] weekDates = getStartAndEndOfWeek(week, year);
        Timestamp startDate = Timestamp.valueOf(weekDates[0].atStartOfDay());
        Timestamp endDate = Timestamp.valueOf(weekDates[1].atTime(23, 59, 59));
        return bookingMapper.toWorkScheduleResponses(bookingRepository.findByStaff_IdAndStartTimeBetweenAndStatus(staff_id, startDate, endDate, BookingStatus.CONFIRMED));
    }
    public List<BookingResponseNoStaff> getBookingsOfStaff(){
        long staffId = SecurityUtils.getCurrentUserId();
        return bookingMapper.toResponseNoStaff(bookingRepository.findByStaff_Id(staffId));
    }
}
