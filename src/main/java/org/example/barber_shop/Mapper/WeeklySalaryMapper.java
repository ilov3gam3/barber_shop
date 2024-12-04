package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Salary.WeeklySalaryAdminResponse;
import org.example.barber_shop.DTO.Salary.WeeklySalaryStaffResponse;
import org.example.barber_shop.Entity.WeeklySalary;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WeeklySalaryMapper {
    List<WeeklySalaryAdminResponse> toWeeklySalaryAdminResponse(List<WeeklySalary> weeklySalaries);
    List<WeeklySalaryStaffResponse> toWeeklySalaryStaffResponse(List<WeeklySalary> weeklySalaries);
}
