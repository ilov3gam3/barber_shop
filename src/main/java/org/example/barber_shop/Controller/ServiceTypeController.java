package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeRequest;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeUpdateRequest;
import org.example.barber_shop.Service.ServiceTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/service-type")
@RequiredArgsConstructor
public class ServiceTypeController {
    private final ServiceTypeService serviceTypeService;

    @PostMapping("/add-service-type")
    public ApiResponse<?> addServiceType(@RequestBody ServiceTypeRequest serviceTypeRequest) {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(), "SERVICE TYPE CREATED", serviceTypeService.addService(serviceTypeRequest)
        );
    }

    @GetMapping("/get-all-service-types")
    public ApiResponse<?> getServiceTypes() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "SERVICE TYPES", serviceTypeService.getAllServiceTypes()
        );
    }
    @PutMapping("")
    public ApiResponse<?> updateServiceType(@RequestBody ServiceTypeUpdateRequest serviceTypeUpdateRequest) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "UPDATED", serviceTypeService.updateServiceType(serviceTypeUpdateRequest)
        );
    }
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteServiceType(@PathVariable Long id) {
        if (serviceTypeService.delete(id)) {
            return new ApiResponse<>(
                    HttpStatus.OK.value(), "DELETED", null
            );
        } else {
            return new ApiResponse<>(
                    HttpStatus.UNPROCESSABLE_ENTITY.value(), "DELETE FAIL", null
            );
        }
    }
}
