package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Combo.ComboRequest;
import org.example.barber_shop.Service.ComboService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/combo")
@RequiredArgsConstructor
public class ComboController {
    private final ComboService comboService;

    @GetMapping("/get-all-combo")
    public ApiResponse<?> getAllCombo() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL COMBOS", comboService.getAllCombo()
        );
    }

    @PostMapping(value = "/add-combo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> addCombo(ComboRequest comboRequest) throws IOException {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(), "COMBO CREATED", comboService.addCombo(comboRequest)
        );
    }
}
