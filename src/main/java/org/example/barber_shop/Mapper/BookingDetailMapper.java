package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.BookingDetail.BookingDetailResponse;
import org.example.barber_shop.Entity.BookingDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingDetailMapper {
    BookingDetailResponse toBookingDetailResponse(BookingDetail bookingDetail);
}
