package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeRequest;
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

}
