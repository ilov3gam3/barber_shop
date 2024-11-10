package org.example.barber_shop.Controller;

import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Booking.BookingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
//    @PostMapping("/book")
//    public ApiResponse<?> book(@RequestBody BookingRequest bookingRequest) {
//        return new ApiResponse<>(
//                HttpStatus.CREATED.value(), "BOOK SUCCESS",
//        );
//    }
}
