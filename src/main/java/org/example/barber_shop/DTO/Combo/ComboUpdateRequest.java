package org.example.barber_shop.DTO.Combo;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
public class ComboUpdateRequest {
    @NotNull(message = "ID is required")
    public long id;

    @NotNull(message = "Service IDs are required")
    public List<Long> serviceIds;

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Description is required")
    public String description;

    @Positive(message = "Price must be a positive value")
    public long price;

    @Min(value = 1, message = "Estimated time must be at least 1 minute")
    public int estimateTime;

    public List<MultipartFile> new_images; // Optional field

    public List<Long> remove_images; // Optional field
}
