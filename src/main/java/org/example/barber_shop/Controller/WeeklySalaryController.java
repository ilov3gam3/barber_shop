package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.Service.SalaryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weekly-salary")
@RequiredArgsConstructor
public class WeeklySalaryController {
    private final SalaryService salaryService;
    @GetMapping("")//admin
    public ApiResponse<?> getSalary(@RequestParam(required = false) Integer week, @RequestParam(required = false) Integer year, @RequestParam(required = false) Long staff_id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "", salaryService.adminGetWeeklySalary(week, year, staff_id)
        );
    }
    @GetMapping("/staff")//staff
    public ApiResponse<?> staffGetSalary(@RequestParam(required = false) Integer week, @RequestParam(required = false) Integer year){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "", salaryService.staffGetWeeklySalary(week, year)
        );
    }
}
