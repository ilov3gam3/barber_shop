package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Shift.AddShiftRequest;
import org.example.barber_shop.DTO.Shift.ShiftRequest;
import org.example.barber_shop.Service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @GetMapping("")
    public ApiResponse<?> getAllShifts() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL SHIFTS", shiftService.getAllShift()
        );
    }
    @PostMapping("")
    public ApiResponse<?> addShift(@RequestBody AddShiftRequest addShiftRequest) {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(), "SHIFT ADDED", shiftService.addShift(addShiftRequest)
        );
    }
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteShift(@PathVariable("id") int id) {
        if (shiftService.deleteShift(id)) {
            return new ApiResponse<>(
                    HttpStatus.OK.value(), "SHIFT DELETED SUCCESS", null
            );
        } else {
            return new ApiResponse<>(
                    HttpStatus.UNPROCESSABLE_ENTITY.value(), "SHIFT DELETED FAIL", null
            );
        }
    }
    @PutMapping("")
    public ApiResponse<?> updateShift(@RequestBody ShiftRequest shiftRequest){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "SHIFT UPDATED", shiftService.updateShift(shiftRequest)
        );
    }
}
