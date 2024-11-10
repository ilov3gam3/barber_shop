package org.example.barber_shop.DTO.Service;

import org.example.barber_shop.DTO.File.FileResponseNoOwner;

import java.sql.Timestamp;
import java.util.List;

public class ServiceResponseNoType {
    public long id;
    public String name;
    public String description;
    public long price;
    public int estimateTime;
    public List<FileResponseNoOwner> images;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
