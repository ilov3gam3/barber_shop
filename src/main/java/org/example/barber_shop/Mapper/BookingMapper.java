package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Booking.BookingResponseAdmin;
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
    @Mapping(source = "review", target = "review")
    @Mapping(source = "review.details", target = "review.reviewDetails")
    List<BookingResponseNoUser> toResponses(List<Booking> bookings);
    List<WorkScheduleResponse> toWorkScheduleResponses(List<Booking> bookings);
    @Mapping(source = "customer", target = "customer")
    List<BookingResponseNoStaff> toResponseNoStaff(List<Booking> bookings);
    List<BookingResponseAdmin> toResponseAdmin(List<Booking> bookings);
    BookingResponseAdmin toResponseAdmin(Booking booking);
    BookingResponseNoStaff toResponseNoStaff(Booking booking);
}
