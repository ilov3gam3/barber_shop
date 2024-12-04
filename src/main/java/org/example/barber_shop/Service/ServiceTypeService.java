package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeRequest;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeResponse;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeResponseNoService;
import org.example.barber_shop.DTO.ServiceType.ServiceTypeUpdateRequest;
import org.example.barber_shop.Entity.ServiceType;
import org.example.barber_shop.Mapper.ServiceTypeMapper;
import org.example.barber_shop.Repository.ServiceTypeRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceTypeService {
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceTypeResponseNoService addService(ServiceTypeRequest serviceTypeRequest) {
        ServiceType serviceType = serviceTypeMapper.toEntity(serviceTypeRequest);
        ServiceType savedServiceType = serviceTypeRepository.save(serviceType);
        return serviceTypeMapper.toServiceTypeResponseNoService(savedServiceType);
    }

    public List<ServiceTypeResponse> getAllServiceTypes() {
        List<ServiceType> serviceTypes = serviceTypeRepository.findByDeletedFalse();
        return serviceTypeMapper.toDto(serviceTypes);
    }
    public ServiceTypeResponseNoService updateServiceType(ServiceTypeUpdateRequest serviceTypeUpdateRequest){
        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(serviceTypeUpdateRequest.id);
        if (serviceTypeOptional.isPresent()) {
            ServiceType serviceType = serviceTypeOptional.get();
            serviceType.setName(serviceTypeUpdateRequest.name);
            return serviceTypeMapper.toServiceTypeResponseNoService(serviceTypeRepository.save(serviceType));
        } else {
            throw new RuntimeException("Service type not found with id " + serviceTypeUpdateRequest.id);
        }
    }
    public boolean delete(long id){
        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(id);
        if (serviceTypeOptional.isPresent()) {
            ServiceType serviceType = serviceTypeOptional.get();
            serviceType.setDeleted(true);
            serviceTypeRepository.save(serviceType);
            return true;
        } else {
            throw new RuntimeException("Service type not found with id " + id);
        }
    }
}
