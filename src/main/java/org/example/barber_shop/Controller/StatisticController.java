package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.Service.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/customers")
    public ApiResponse<?> topCustomers(@RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "TOP CUSTOMERS", statisticService.getTopCustomers(fromDate, toDate)
        );
    }

    @GetMapping("/booking/service-popularity")
    public ApiResponse<?> servicePopularity(@RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "TOP SERVICES", statisticService.getTopServices(fromDate, toDate)
        );
    }
    @GetMapping("/booking/combo-popularity")
    public ApiResponse<?> comboPopularity(@RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "TOP COMBOS", statisticService.getTopCombos(fromDate, toDate)
        );
    }

    @GetMapping("/booking")
    public ApiResponse<?> booking(@RequestParam(required = false) Integer year){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "BOOKING STATISTIC", statisticService.getYearStatistic(year)
        );
    }
    @GetMapping("/staffs")
    public ApiResponse<?> getTopStaffs(@RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "TOP STAFFS", statisticService.getTopStaffs(fromDate, toDate)
        );
    }
}
