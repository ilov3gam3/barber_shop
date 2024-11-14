package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Shift.StaffShiftRequest;
import org.example.barber_shop.Service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @PostMapping("/add-staff-shift")
    public ApiResponse<?> addStaffShift(@RequestBody StaffShiftRequest staffShiftRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "STAFF SHIFT ADDED", shiftService.addStaffShift(staffShiftRequest)
        );
    }

    @GetMapping("/get-all-shifts")
    public ApiResponse<?> getAllShifts() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL SHIFTS", shiftService.getAllShift()
        );
    }


}
