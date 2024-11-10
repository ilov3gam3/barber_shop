package org.example.barber_shop.DTO.Combo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ComboRequest {
    public List<Long> serviceIds;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public List<MultipartFile> images;
}
