package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Mapper.BookingMapper;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.DTO.Booking.BookingRequest;
import org.example.barber_shop.DTO.Booking.BookingResponse;
import org.example.barber_shop.Exception.UserNotFoundException;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Util.TimeUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
        List<Booking> bookings = bookingRepository.findByStaff_IdAndStatus(staff.getId(), BookingStatus.CONFIRMED);
        if (bookings.isEmpty()) {
            return true;
        } else {
            for (Booking booking : bookings) {
                Timestamp existingStartTime = booking.getStartTime();
                Timestamp existingEndTime = booking.getEndTime();
                if (startTime.before(existingEndTime) && endTime.after(existingStartTime)) {
                    return false;
                }
            }
        }
        return true;
    }

    public BookingResponse addBooking(BookingRequest bookingRequest) {
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
    public List<BookingResponse> getBookingsOfCustomers(){
        long userId = SecurityUtils.getCurrentUserId();
        List<Booking> bookings = bookingRepository.findByCustomer_Id(userId);
        return bookingMapper.toResponses(bookings);
    }
    public BookingResponse confirmBooking(long booking_id){
        Optional<Booking> booking = bookingRepository.findById(booking_id);
        if (booking.isPresent()) {
            Booking checkedBooking = booking.get();
            checkedBooking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(checkedBooking);
            return bookingMapper.toResponse(checkedBooking);
        } else {
            throw new UserNotFoundException("Booking not found.");
        }
    }
}
