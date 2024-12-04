package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Salary.StaffSalaryUpdateRequest;
import org.example.barber_shop.Service.SalaryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;
    @PutMapping("")
    public ApiResponse<?> updateSalaryStaff(@RequestBody StaffSalaryUpdateRequest staffSalaryUpdateRequest){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF SALARY UPDATED", salaryService.updateStaffSalary(staffSalaryUpdateRequest)
        );
    }
    @GetMapping("")
    public ApiResponse<?> getSalaryStaff(){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF SALARY", salaryService.getAllStaffSalary()
        );
    }
    @GetMapping("/{id}")
    public ApiResponse<?> getSalaryStaffById(@PathVariable("id") long id){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF SALARY ID " + id, salaryService.getAStaffSalary(id)
        );
    }
}
