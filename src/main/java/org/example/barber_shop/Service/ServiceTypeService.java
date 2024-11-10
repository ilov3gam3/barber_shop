package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeRequest;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeResponse;
import org.example.barber_shop.Entity.ServiceType;
import org.example.barber_shop.Mapper.ServiceTypeMapper;
import org.example.barber_shop.Repository.ServiceTypeRepository;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceTypeService {
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceTypeResponse addService(ServiceTypeRequest serviceTypeRequest) {
        ServiceType serviceType = serviceTypeMapper.toEntity(serviceTypeRequest);
        ServiceType savedServiceType = serviceTypeRepository.save(serviceType);
        return serviceTypeMapper.toDto(savedServiceType);
    }

    public List<ServiceTypeResponse> getAllServiceTypes() {
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        return serviceTypeMapper.toDto(serviceTypes);
    }
}
