package org.example.barber_shop.DTO.AI;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeHairStyle {
    public MultipartFile image;
    public String style;
    public String color;
}
