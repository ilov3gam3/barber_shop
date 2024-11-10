package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.ServiceType.ServiceTypeRequest;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeResponse;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeResponseNoService;
import org.example.barber_shop.Entity.ServiceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceTypeMapper {
    ServiceType toEntity(ServiceTypeRequest serviceTypeRequest);
    ServiceTypeResponse toDto(ServiceType serviceType);
    @Mapping(source = "services", target = "services")
    List<ServiceTypeResponse> toDto(List<ServiceType> serviceTypes);
    ServiceTypeResponseNoService toServiceTypeResponseNoService(ServiceType serviceType);
}
