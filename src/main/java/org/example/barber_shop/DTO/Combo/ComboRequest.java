package org.example.barber_shop.DTO.Combo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComboRequest {
    public List<Long> serviceIds;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public List<MultipartFile> images;
}
