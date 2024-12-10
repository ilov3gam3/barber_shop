package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.AI.Request;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.Service.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
