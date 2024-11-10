package org.example.barber_shop.DTO.Combo;

import org.example.barber_shop.DTO.File.FileResponseNoOwner;
import org.example.barber_shop.DTO.Service.ServiceResponseNoType;

import java.sql.Timestamp;
import java.util.List;

public class ComboResponse {
    public long id;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public List<FileResponseNoOwner> images;
    public List<ServiceResponseNoType> services;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
