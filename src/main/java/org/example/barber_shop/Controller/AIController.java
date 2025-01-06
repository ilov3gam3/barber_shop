package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.AI.AllowValues;
import org.example.barber_shop.DTO.AI.ChangeHairStyle;
import org.example.barber_shop.DTO.AI.ProRequest;
import org.example.barber_shop.DTO.AI.Request;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.Service.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/AI")
@RequiredArgsConstructor
public class AIController {
    private final GeminiService geminiService;
    @PostMapping("")
    public ApiResponse<?> recommendHairStyle(@RequestBody Request request) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "HAIR STYLES", geminiService.askAI(request)
        );
    }

    @PostMapping(value = "/pro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> recommendPro(@ModelAttribute ProRequest request) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "RESULT", geminiService.askGpt(request)
        );
    }
    @PostMapping(value = "/change-hair-style", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> changeHairStyle(@ModelAttribute ChangeHairStyle request) throws IOException {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "RESULT", geminiService.changeHairStyle2(request)
        );
    }
    @GetMapping("/allowed-values")
    public ApiResponse<?> recommendAllowedValues() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALLOWED VALUES", new AllowValues()
        );
    }
}
