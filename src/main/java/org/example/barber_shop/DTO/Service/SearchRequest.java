package org.example.barber_shop.DTO.Service;

import lombok.Data;

@Data
public class SearchRequest {
    String name;
    String description;
    Integer estimateTimeMin;
    Integer estimateTimeMax;
    Long priceMin;
    Long priceMax;
    Integer serviceType;
}
