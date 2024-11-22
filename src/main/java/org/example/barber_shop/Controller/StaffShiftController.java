package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Shift.StaffShiftRequest;
import org.example.barber_shop.Service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff-shift")
@RequiredArgsConstructor
public class StaffShiftController {
    private final ShiftService shiftService;
    @PostMapping("")
    public ApiResponse<?> addStaffShift(@RequestBody StaffShiftRequest staffShiftRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF SHIFT ADDED", shiftService.addStaffShift(staffShiftRequest)
        );
    }
    @GetMapping("")
    public ApiResponse<?> adminGetShifts(@RequestParam(required = false) Integer week, @RequestParam(required = false) Integer year) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL STAFF SHIFTS IN WEEK", shiftService.adminGetShiftsInWeek(week, year)
        );
    }
    @GetMapping("/get-staff-shift")
    public ApiResponse<?> staffGetShifts(@RequestParam(required = false) Integer week, @RequestParam(required = false) Integer year, @RequestParam(required = false) long staff_id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "SHIFTS OF STAFF IN WEEK", shiftService.staffGetShifts(week, year, staff_id)
        );
    }
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable long id) {
        if (shiftService.delete(id)){
            return new ApiResponse<>(
                    HttpStatus.OK.value(), "DELETED", null
            );
        } else {
            return new ApiResponse<>(
                    HttpStatus.OK.value(), "DELETE FAIL", null
            );
        }
    }
}
