package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.DTO.Booking.*;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Mapper.BookingMapper;
import org.example.barber_shop.Mapper.ReviewDetailMapper;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Util.TimeUtil;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final ServiceRepository serviceRepository;
    private final ComboRepository comboRepository;
    private final BookingMapper bookingMapper;
    private final StaffShiftRepository staffShiftRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewDetailMapper reviewDetailMapper;

    public boolean isTimeValid(User staff, Timestamp startTime, Timestamp endTime) {
        List<Booking> bookings1 = bookingRepository.findByStaff_IdAndStatusAndStartTimeBeforeAndEndTimeAfter(staff.getId(), BookingStatus.PAID, endTime, startTime);
        List<Booking> bookings2 = bookingRepository.findByStaff_IdAndStatusAndStartTimeBeforeAndEndTimeAfter(staff.getId(), BookingStatus.PENDING, endTime, startTime);
        return bookings1.isEmpty() && bookings2.isEmpty();
    }

    public boolean isTimeInAssignedShift(User staff, Timestamp startTime, Timestamp endTime) {
        LocalDate localDate = startTime.toLocalDateTime().toLocalDate();
        List<StaffShift> staffShifts = staffShiftRepository.findByStaffIdAndDate(staff.getId(), localDate);
        List<Shift> shifts = staffShifts.stream()
                .map(StaffShift::getShift)
                .sorted(Comparator.comparing(Shift::getStartTime))
                .toList();
        List<TimeBlock> timeBlocks = new ArrayList<>();
        LocalTime blockStart = null;
        LocalTime blockEnd = null;
        for (Shift shift : shifts) {
            if (blockStart == null) {
                blockStart = shift.getStartTime();
                blockEnd = shift.getEndTime();
            } else if (!shift.getStartTime().isAfter(blockEnd)) {
                blockEnd = shift.getEndTime().isAfter(blockEnd) ? shift.getEndTime() : blockEnd;
            } else {
                timeBlocks.add(new TimeBlock(blockStart, blockEnd));
                blockStart = shift.getStartTime();
                blockEnd = shift.getEndTime();
            }
        }
        if (blockStart != null) {
            timeBlocks.add(new TimeBlock(blockStart, blockEnd));
        }
        for (TimeBlock block : timeBlocks) {
            if (timeOverlaps(block.startTime, block.endTime, startTime.toLocalDateTime().toLocalTime(), endTime.toLocalDateTime().toLocalTime())) {
                return true; // Valid booking
            }
        }
        return false;
    }

    private boolean timeOverlaps(LocalTime blockStart, LocalTime blockEnd, LocalTime bookingStart, LocalTime bookingEnd) {
        return !(bookingStart.isBefore(blockStart) || bookingEnd.isAfter(blockEnd));
    }

    private static class TimeBlock {
        LocalTime startTime;
        LocalTime endTime;

        public TimeBlock(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public BookingResponseNoUser addBooking(BookingRequest bookingRequest) {
        if (bookingRequest.startTime.after(new Timestamp(System.currentTimeMillis()))) {
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
                    if (isTimeInAssignedShift(staff_checked, bookingRequest.startTime, endTime)) {
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
                        bookingRepository.save(booking);
                        return bookingMapper.toResponse(booking);
                    } else {
//                        throw new LocalizedException("estimate.end.time.staff.no.shift", endTime, staff_checked.getName(), bookingRequest.startTime, endTime);
                        throw new RuntimeException("Estimate end time is " + endTime + ".Staff "+ staff_checked.getName() +" has no shift from " + bookingRequest.startTime + " to " + endTime);
                    }
                } else {
                    throw new RuntimeException("Estimate end time is " + endTime + ".Conflict with staff's time, "+ staff_checked.getName() +" already have a booking from " + bookingRequest.startTime + " to " + endTime);
                }
            } else {
                throw new RuntimeException("Staff not found.");
            }
        } else {
            throw new RuntimeException("Start time must be in the future.");
        }

    }

    public List<BookingResponseNoUser> getBookingsOfCustomers() {
        long userId = SecurityUtils.getCurrentUserId();
        List<Booking> bookings = bookingRepository.findByCustomer_Id(userId);
        List<BookingResponseNoUser> bookingResponseNoUsers = bookingMapper.toResponses(bookings);
        for (int i = 0; i < bookingResponseNoUsers.size(); i++) {
            if (bookings.get(i).getReview() != null){
                bookingResponseNoUsers.get(i).review.reviewDetails = reviewDetailMapper.toReviewDetailResponseList(bookings.get(i).getReview().getDetails());
            }
        }
        return bookingResponseNoUsers;
    }

    /*public BookingResponseNoUser confirmBooking(long booking_id){
        long staff_id = SecurityUtils.getCurrentUserId();
        Booking booking = bookingRepository.findByIdAndStatusAndStaff_Id(booking_id, BookingStatus.PENDING, staff_id);
        if (booking != null) {
            List<Booking> confirmedBookingsOfAStaff = bookingRepository.findByStaff_IdAndStatusAndStartTimeBeforeAndEndTimeAfter(staff_id, BookingStatus.CONFIRMED, booking.getEndTime(), booking.getStartTime());
            if (confirmedBookingsOfAStaff.isEmpty()) {
                booking.setStatus(BookingStatus.CONFIRMED);
                booking = bookingRepository.save(booking);

                Notification notification = new Notification();
                notification.setUser(booking.getCustomer());
                notification.setType(NotificationType.UNPAID_BOOKING_REMINDER);
                notification.setTitle("Booking confirmed");
                notification.setMessage(booking.getStaff().getName() + " has confirm your booking. You can pay for the booking now.");
                notification.setTargetUrl(""); // Optionally, link to payment page
                notification.setSeen(false);
                notification = notificationRepository.save(notification);
                notification.setUser(null);
                simpMessagingTemplate.convertAndSendToUser(booking.getCustomer().getEmail(), "/topic", notification);
                return bookingMapper.toResponse(booking);
            } else {
                throw new RuntimeException("You already has a confirmed booking in this time.");
            }
        } else {
            throw new RuntimeException("Booking not found, or it's not on pending status.");
        }
    }*/
    public LocalDate[] getStartAndEndOfWeek(int week, int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startDate = startOfYear
                .with(weekFields.weekOfYear(), week)
                .with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));

        LocalDate endDate = startDate.plusDays(6);
        return new LocalDate[]{startDate, endDate};
    }

    public List<WorkScheduleResponse> getStaffWorkScheduleInWeek(Integer week, Integer year, long staff_id) {
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
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startDateLocal = LocalDate.ofYearDay(year, 1)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1); // Monday
        LocalDate endDateLocal = startDateLocal.plusDays(6); // Sunday
        List<StaffShift> staffShifts = staffShiftRepository.findByStaffIdAndDateBetween(staff_id, startDateLocal, endDateLocal);
        return bookingMapper.toWorkScheduleResponses(bookingRepository.findByStaff_IdAndStartTimeBetweenAndStatus(staff_id, startDate, endDate, BookingStatus.PAID));
    }

    public List<BookingResponseNoStaff> getBookingsOfStaff() {
        long staffId = SecurityUtils.getCurrentUserId();
        List<Booking> bookings = bookingRepository.findByStaff_Id(staffId);
        List<BookingResponseNoStaff> bookingResponseNoStaffs = bookingMapper.toResponseNoStaff(bookings);
        for (int i = 0; i < bookingResponseNoStaffs.size(); i++) {
            if (bookings.get(i).getReview() != null){
                bookingResponseNoStaffs.get(i).review.reviewDetails = reviewDetailMapper.toReviewDetailResponseList(bookings.get(i).getReview().getDetails());
            }
        }
        return bookingResponseNoStaffs;
    }

    public BookingResponseNoUser adminBook(BookingRequest bookingRequest) {
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
                if (isTimeInAssignedShift(staff_checked, bookingRequest.startTime, endTime)) {
                    User customer = SecurityUtils.getCurrentUser(); // admin
                    Booking booking = new Booking();
                    booking.setStatus(BookingStatus.PAID);
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
                    Payment payment = new Payment();
                    long temp_price = 0;
                    for (BookingDetail bookingDetail : savedBookingDetails) {
                        if (bookingDetail.getService() != null) {
                            temp_price += bookingDetail.getService().getPrice();
                            bookingDetail.setFinalPrice(bookingDetail.getService().getPrice());
                        }
                        if (bookingDetail.getCombo() != null) {
                            temp_price += bookingDetail.getCombo().getPrice();
                            bookingDetail.setFinalPrice(bookingDetail.getCombo().getPrice());
                        }
                    }
                    booking.setTotalPrice(temp_price);
                    payment.setAmount(temp_price);
                    payment.setPaid_at(new Timestamp(System.currentTimeMillis()));
                    payment.setCardType("CASH");
                    paymentRepository.save(payment);
                    booking.setPayment(payment);
                    bookingRepository.save(booking);
                    return bookingMapper.toResponse(booking);
                } else {
                    throw new RuntimeException("Staff has no shift on this time.");
                }
            } else {
                throw new RuntimeException("Conflict with staff's time, staff already have a booking in this time.");
            }
        } else {
            throw new RuntimeException("Staff not found.");
        }
    }

    public void cancelBooking(long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            Booking checkedBooking = booking.get();
            if (checkedBooking.getStatus() == BookingStatus.PENDING) {
                checkedBooking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(checkedBooking);
            } else {
                throw new RuntimeException("Booking can only be canceled if in PENDING status.");
            }
        } else {
            throw new RuntimeException("Booking not found.");
        }
    }

    public BookingResponseNoUser updateBooking(BookingUpdateRequest bookingUpdateRequest) {
        User user = SecurityUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndCustomerAndStatus(bookingUpdateRequest.bookingId, user, BookingStatus.PENDING);
        if (booking != null) {
            Optional<User> staff = userRepository.findById(bookingUpdateRequest.staff_id);
            if (staff.isPresent()) {
                User staff_checked = staff.get();
                booking.setStaff(staff_checked);
                List<Service> services = serviceRepository.findAllById(bookingUpdateRequest.serviceIds);
                List<Combo> combos = comboRepository.findAllById(bookingUpdateRequest.comboIds);
                int tempTime = 0;
                for (Service service : services) {
                    tempTime += service.getEstimateTime();
                }
                for (Combo combo : combos) {
                    tempTime += combo.getEstimateTime();
                }
                Timestamp endTime = TimeUtil.calculateEndTime(bookingUpdateRequest.startTime, tempTime);
                if (isTimeValid(staff_checked, bookingUpdateRequest.startTime, endTime)) {
                    if (isTimeInAssignedShift(staff_checked, bookingUpdateRequest.startTime, endTime)) {
                        booking.setNote(bookingUpdateRequest.note);
                        booking.setStartTime(bookingUpdateRequest.startTime);
                        booking.setEndTime(endTime);
                        List<BookingDetail> newBookingDetails = new ArrayList<>();
                        for (Service service : services) {
                            newBookingDetails.add(new BookingDetail(booking, service));
                        }
                        for (Combo combo : combos) {
                            newBookingDetails.add(new BookingDetail(booking, combo));
                        }
                        List<BookingDetail> oldBookingDetails = booking.getBookingDetails();
                        bookingDetailRepository.deleteAll(oldBookingDetails);
                        newBookingDetails = bookingDetailRepository.saveAll(newBookingDetails);
                        booking.setBookingDetails(newBookingDetails);
                        return bookingMapper.toResponse(bookingRepository.save(booking));
                    } else {
                        throw new RuntimeException("Staff has no shift on this time.");
                    }
                } else {
                    throw new RuntimeException("Conflict with staff's time, staff already have a booking in this time.");
                }
            } else {
                throw new RuntimeException("Staff not found.");
            }
        } else {
            throw new RuntimeException("Booking not found or can not be update now.");
        }
    }

    public List<BookingResponseAdmin> adminGetBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingResponseAdmin> bookingResponseAdmins = bookingMapper.toResponseAdmin(bookings);
        for (int i = 0; i < bookingResponseAdmins.size(); i++) {
            if (bookings.get(i).getReview() != null){
                bookingResponseAdmins.get(i).review.reviewDetails = reviewDetailMapper.toReviewDetailResponseList(bookings.get(i).getReview().getDetails());
            }
        }
        return bookingResponseAdmins;
    }

    public BookingResponseNoUser updateBooking(long id) {
        User staff = SecurityUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndStaff(id, staff);
        if (booking != null) {
            if (booking.getStatus() == BookingStatus.PAID) {
                if (booking.getEndTime().before(new Timestamp(System.currentTimeMillis()))) {
                    booking.setStatus(BookingStatus.COMPLETED);
                    return bookingMapper.toResponse(bookingRepository.save(booking));
                } else {
                    throw new RuntimeException("Booking can only be set to COMPLETED after booking's end time.");
                }
            } else {
                throw new RuntimeException("Booking can only be complete if in PAID status.");
            }
        } else {
            throw new RuntimeException("Booking not found, or this is not your booking.");
        }
    }

    public BookingResponseNoUser noShowBooking(long id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            if (booking.getStatus() == BookingStatus.PAID) {
                if (booking.getEndTime().before(new Timestamp(System.currentTimeMillis()))) {
                    booking.setStatus(BookingStatus.NO_SHOW);
                    return bookingMapper.toResponse(bookingRepository.save(booking));
                } else {
                    throw new RuntimeException("Booking can only be set to NO_SHOW after booking's end time.");
                }
            } else {
                throw new RuntimeException("Booking can only be set to NO_SHOW if in PAID status.");
            }
        } else {
            throw new RuntimeException("Booking not found.");
        }
    }
    public BookingResponseAdmin getBookingWithId(long id){
        User user = SecurityUtils.getCurrentUser();
        switch (user.getRole()){
            case ROLE_ADMIN -> {
                Optional<Booking> bookingOptional = bookingRepository.findById(id);
                if (bookingOptional.isPresent()) {
                    return bookingMapper.toResponseAdmin(bookingOptional.get());
                } else {
                    throw new RuntimeException("Booking not found with id " + id + " or you don't have permission to access this resource!");
                }
            }
            case ROLE_CUSTOMER -> {
                Booking booking = bookingRepository.findByIdAndCustomer(id, user);
                if (booking != null) {
                    return bookingMapper.toResponseAdmin(booking);
                }
                else {
                    throw new RuntimeException("Booking not found with id " + id + " or you don't have permission to access this resource!");
                }
            }
            case ROLE_STAFF -> {
                Booking booking = bookingRepository.findByIdAndStaff(id, user);
                if (booking != null) {
                    return bookingMapper.toResponseAdmin(booking);
                }
                else {
                    throw new RuntimeException("Booking not found with id " + id + " or you don't have permission to access this resource!");
                }
            }
            default -> {
                throw new RuntimeException("Something went wrong.");
            }
        }
    }
    public BookingResponseNoStaff rejectBooking(long id){
        User staff = SecurityUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndStatusAndStaff_Id(id, BookingStatus.PENDING, staff.getId());
        if (booking != null) {
            booking.setStatus(BookingStatus.REJECTED);
            return bookingMapper.toResponseNoStaff(bookingRepository.save(booking));
        } else {
            throw new RuntimeException("Booking not found.");
        }
    }
}
