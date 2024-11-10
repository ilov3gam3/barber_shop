package org.example.barber_shop.DTO.Service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ServiceRequest {
    public long serviceTypeId;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public List<MultipartFile> images;
}
