package org.example.barber_shop.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Payment.PaymentRequest;
import org.example.barber_shop.Service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/get-vnpay-url")
    public ApiResponse<?> getVNPayUrl(@ModelAttribute PaymentRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "VNPAY URL", paymentService.getVnpayUrl(paymentRequest, request)
        );
    }
    @GetMapping("/vnpay-result")
    public ApiResponse<?> getVNPayResult(HttpServletRequest request) throws UnsupportedEncodingException {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "HMM", paymentService.handleVnpayResult(request)
        );
    }
}
