package org.example.barber_shop.DTO.BookingDetail;

import org.example.barber_shop.DTO.Combo.ComboResponse;
import org.example.barber_shop.DTO.Service.ServiceResponseNoType;

import java.sql.Timestamp;

public class BookingDetailResponse {
    public long id;
    public ServiceResponseNoType service;
    public ComboResponse combo;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
