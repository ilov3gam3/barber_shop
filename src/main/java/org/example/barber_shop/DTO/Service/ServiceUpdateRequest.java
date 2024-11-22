package org.example.barber_shop.DTO.Service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
public class ServiceUpdateRequest {
    @NotNull(message = "ID is required")
    public long id;

    @NotNull(message = "Service Type ID is required")
    public long serviceTypeId;

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Description is required")
    public String description;

    @NotNull(message = "Price is required")
    public long price;

    @NotNull(message = "Estimated time is required")
    public int estimateTime;
    public List<MultipartFile> new_images;
    public List<Long> remove_images;
}
