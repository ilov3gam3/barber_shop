package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.Service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("")
    public ApiResponse<?> getAllNotification(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "NOTIFICATIONS", notificationService.getAllNotifications(page, size)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateNotification(@PathVariable long id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "NOTIFICATION " + id + " MARK AS SEEN", notificationService.markSeen(id)
        );
    }
}
