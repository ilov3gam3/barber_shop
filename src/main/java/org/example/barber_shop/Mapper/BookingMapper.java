package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Booking.BookingResponse;
import org.example.barber_shop.Entity.Booking;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponse toResponse(Booking booking);
    List<BookingResponse> toResponses(List<Booking> bookings);
}
