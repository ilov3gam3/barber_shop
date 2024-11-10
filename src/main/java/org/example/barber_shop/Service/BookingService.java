package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.Booking.BookingRequest;
import org.example.barber_shop.DTO.Booking.BookingResponse;
import org.example.barber_shop.Repository.BookingDetailRepository;
import org.example.barber_shop.Repository.BookingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
//    public BookingResponse addBooking(BookingRequest bookingRequest) {
//
//    }
}
