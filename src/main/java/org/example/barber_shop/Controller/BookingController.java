package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Booking.BookingRequest;
import org.example.barber_shop.Service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book")
    public ApiResponse<?> book(@RequestBody BookingRequest bookingRequest) {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(), "BOOK SUCCESS", bookingService.addBooking(bookingRequest)
        );
    }
    @GetMapping("/customer-get-bookings")
    public ApiResponse<?> getBookings() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "YOUR BOOKINGS", bookingService.getBookingsOfCustomers()
        );
    }
    @GetMapping("/staff-get-bookings")
    public ApiResponse<?> getBookingsOfStaff() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF BOOKINGS", bookingService.getBookingsOfStaff()
        );
    }
    @GetMapping("/confirm-booking")
    public ApiResponse<?> confirmBooking(@RequestParam long booking_id){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "CONFIRM BOOKING", bookingService.confirmBooking(booking_id)
        );
    }

    @GetMapping("/get-staff-work-schedule-in-week")
    public ApiResponse<?> getStaffWorkScheduleWeek(@RequestParam(required = false) Integer week, @RequestParam(required = false) Integer year, @RequestParam long staff_id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF WORK SCHEDULE",bookingService.getStaffWorkScheduleInWeek(week, year, staff_id)
            );
    }
    /*@PostMapping("/admin-book")
    public ApiResponse<?> adminBook(@RequestBody BookingRequest bookingRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ADMIN BOOK SUCCESS"
        )
    }*/
}
