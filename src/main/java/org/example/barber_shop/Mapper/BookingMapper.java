package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Booking.BookingResponseNoStaff;
import org.example.barber_shop.DTO.Booking.BookingResponseNoUser;
import org.example.barber_shop.DTO.Booking.WorkScheduleResponse;
import org.example.barber_shop.Entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponseNoUser toResponse(Booking booking);
    List<BookingResponseNoUser> toResponses(List<Booking> bookings);
    List<WorkScheduleResponse> toWorkScheduleResponses(List<Booking> bookings);
    @Mapping(source = "customer", target = "customer")
    List<BookingResponseNoStaff> toResponseNoStaff(List<Booking> bookings);
}
