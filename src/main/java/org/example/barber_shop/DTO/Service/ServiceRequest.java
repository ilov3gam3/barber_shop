package org.example.barber_shop.DTO.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {
    public long serviceTypeId;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public List<MultipartFile> images;
}
