package org.example.barber_shop.Exception;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;


    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ApiResponse<?>> localizedException(LocalizedException e) {
        e.printStackTrace();
        String messageVi = messageSource.getMessage(e.getMessageKey(), e.getArgs(), new Locale("vi"));
        System.err.println(messageVi);
        String messageKo = messageSource.getMessage(e.getMessageKey(), e.getArgs(), new Locale("ko"));
        Map<String, String> map = Map.of("vi", messageVi, "ko", messageKo);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), map);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        e.printStackTrace();
        Map<String, String> map = Map.of("vi", "Lỗi hệ thống", "ko", "시스템 오류", "debug", e.getMessage());
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), map);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
