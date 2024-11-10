package org.example.barber_shop.DTO.Service;

import org.example.barber_shop.DTO.File.FileResponseNoOwner;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeResponseNoService;

import java.sql.Timestamp;
import java.util.List;

public class ServiceResponse {
    public long id;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public ServiceTypeResponseNoService serviceType;
    public List<FileResponseNoOwner> images;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
