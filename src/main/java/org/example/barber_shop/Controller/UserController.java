package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.User.UpdateProfileRequest;
import org.example.barber_shop.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @GetMapping("/")
    public ApiResponse<?> getAllUsers() {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "ALL USERS",
                userService.getAllUsers()
        );
    }

    @GetMapping("/profile")
    public ApiResponse<?> getUserProfile() {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "YOUR PROFILE",
                userService.getProfile()
        );
    }

    @PutMapping("/profile")
    public ApiResponse<?> updateUserProfile(@RequestBody UpdateProfileRequest updateProfileRequest){
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "PROFILE UPDATED",
                userService.updateProfile(updateProfileRequest)
        );
    }
    @PostMapping("/update-avatar")
    public ApiResponse<?> updateAvatar(@RequestParam("image") MultipartFile image) throws IOException {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "AVATAR LOADED",
                userService.updateAvatar(image)
        );
    }
}
