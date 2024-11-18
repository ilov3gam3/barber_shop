package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Shift.ShiftRequest;
import org.example.barber_shop.Service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @GetMapping("/get-all-shifts")
    public ApiResponse<?> getAllShifts() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL SHIFTS", shiftService.getAllShift()
        );
    }
    @PutMapping("/update-shift")
    public ApiResponse<?> updateShift(@RequestBody ShiftRequest shiftRequest){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "SHIFT UPDATED", shiftService.updateShift(shiftRequest)
        );
    }
}
