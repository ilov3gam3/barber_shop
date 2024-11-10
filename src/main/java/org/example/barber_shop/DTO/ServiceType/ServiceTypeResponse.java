package org.example.barber_shop.DTO.ServiceType;

import org.example.barber_shop.DTO.Service.ServiceResponseNoType;

import java.sql.Timestamp;
import java.util.List;

public class ServiceTypeResponse {
    public long id;
    public String name;
    public List<ServiceResponseNoType> services;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
