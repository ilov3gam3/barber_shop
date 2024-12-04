package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Salary.StaffSalaryResponse;
import org.example.barber_shop.Entity.StaffSalary;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffSalaryMapper{
    StaffSalaryResponse toStaffSalaryResponse(StaffSalary staffSalary);
    List<StaffSalaryResponse> toStaffSalaryResponses(List<StaffSalary> staffSalaries);
}
