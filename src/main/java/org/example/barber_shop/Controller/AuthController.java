package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.User.ForgotPasswordRequest;
import org.example.barber_shop.DTO.User.LoginRequest;
import org.example.barber_shop.DTO.User.RegisterRequest;
import org.example.barber_shop.DTO.User.ResetPasswordRequest;
import org.example.barber_shop.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest registerRequest) {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "RESOURCE_CREATED",
                userService.register(registerRequest)
        );
    }

    @GetMapping("/verify-email")
    public ApiResponse<?> verifyEmail(@RequestParam("token") String token) {
        if (userService.verifyToken(token)){
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "RESOURCE_UPDATED",
                    null
            );
        } else {
            return new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "INVALID_TOKEN",
                    null
            );
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest loginRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "LOGIN_SUCCESS",
                userService.login(loginRequest)
        );
    }
    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        userService.forgotPassword(forgotPasswordRequest);
        return new ApiResponse<>(
                HttpStatus.OK.value(), "MAIL SENT", null
        );
    }
    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        userService.resetPassword(resetPasswordRequest);
        return new ApiResponse<>(
                HttpStatus.OK.value(), "PASSWORD RESET", null
        );
    }
}
