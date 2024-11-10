package org.example.barber_shop.Exception;

import org.example.barber_shop.DTO.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailExistException.class)
    public ApiResponse<?> emailExistException(EmailExistException e) {
        e.printStackTrace();
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(PhoneExistException.class)
    public ApiResponse<?> phoneExistException(PhoneExistException e) {
        e.printStackTrace();
        return new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage(), null);
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ApiResponse<?> passwordMismatchException(PasswordMismatchException e) {
        e.printStackTrace();
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> exception(Exception e) {
        e.printStackTrace();
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> illegalArgumentException(IllegalArgumentException e) {
        e.printStackTrace();
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }
}
