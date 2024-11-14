package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Shift.StaffShiftResponse;
import org.example.barber_shop.Entity.StaffShift;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StaffShiftMapper {
    StaffShiftResponse toStaffShiftResponse(StaffShift staffShift);
}
