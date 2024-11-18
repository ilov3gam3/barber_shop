package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Shift.AdminShiftResponse;
import org.example.barber_shop.DTO.Shift.StaffShiftResponse;
import org.example.barber_shop.Entity.Shift;
import org.example.barber_shop.Entity.StaffShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffShiftMapper {
    @Mapping(source = "date", target = "date")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    StaffShiftResponse toStaffShiftResponse(StaffShift staffShift);

    List<StaffShiftResponse> toStaffShiftResponses(List<StaffShift> staffShifts);
    @Mapping(source = "staff", target = "staff")
    AdminShiftResponse toAdminShiftResponse(StaffShift shift);
    List<AdminShiftResponse> toAdminShiftResponses(List<StaffShift> staffShifts);
}
