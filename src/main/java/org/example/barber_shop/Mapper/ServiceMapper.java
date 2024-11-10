package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Service.ServiceRequest;
import org.example.barber_shop.DTO.Service.ServiceResponse;
import org.example.barber_shop.DTO.Service.ServiceResponseNoType;
import org.example.barber_shop.Entity.Service;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    Service toEntity(ServiceRequest serviceRequest);
    ServiceResponse toResponse(Service service);
    ServiceResponseNoType toResponseNoType(Service service);
    List<ServiceResponse> toResponseList(List<Service> services);
}
