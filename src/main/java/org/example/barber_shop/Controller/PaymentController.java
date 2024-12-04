package org.example.barber_shop.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Payment.PaymentRequest;
import org.example.barber_shop.Service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @Value("${front_end_server}")
    private String fe_server;
    @GetMapping("/get-vnpay-url")
    public ApiResponse<?> getVNPayUrl(@ModelAttribute PaymentRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "VNPAY URL", paymentService.getVnpayUrl(paymentRequest, request)
        );
    }
    @GetMapping("/vnpay-result")
    public void getVNPayResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(fe_server + "?payment_status=" + paymentService.handleVnpayResult(request));
    }

    @GetMapping("")
    public ApiResponse<?> getPayments(){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "PAYMENTS", paymentService.getPayments()
        );
    }
    @GetMapping("/{id}")
    public ApiResponse<?> getPayments(@PathVariable long id){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "PAYMENTS", paymentService.getAPayment(id)
        );
    }
    @PutMapping("/cash/{id}")
    public ApiResponse<?> cashPayment(@PathVariable long id){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "PAID", paymentService.cashPayment(id)
        );
    }
}
