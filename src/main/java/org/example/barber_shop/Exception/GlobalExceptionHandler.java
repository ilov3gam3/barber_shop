package org.example.barber_shop.Exception;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;


    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ApiResponse<?>> localizedException(LocalizedException e) {
        e.printStackTrace();
        String messageEn = messageSource.getMessage(e.getMessageKey(), e.getArgs(), Locale.ENGLISH);
        String messageKo = messageSource.getMessage(e.getMessageKey(), e.getArgs(), new Locale("ko"));
        String messageVi = messageSource.getMessage(e.getMessageKey(), e.getArgs(), new Locale("vi"));
        Map<String, String> map = Map.of("en", messageEn, "ko", messageKo, "vi", messageVi);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), map);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        e.printStackTrace();
        Map<String, String> map = Map.of("en", "System error.", "ko", "시스템 오류", "vi", "Lỗi hệ thống.", "debug", e.getMessage());
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), map);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> noResourceFoundException(NoResourceFoundException e) {
        e.printStackTrace();
        ApiResponse<?> apiResponse = new ApiResponse<>( HttpStatus.NOT_FOUND.value(), "THE API YOU LOOKING FOR NOT FOUND", null);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
}
