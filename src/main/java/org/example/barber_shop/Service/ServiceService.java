package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Config.SecurityUtils;
import org.example.barber_shop.DTO.Service.ServiceRequest;
import org.example.barber_shop.DTO.Service.ServiceResponse;
import org.example.barber_shop.Entity.File;
import org.example.barber_shop.Entity.Service;
import org.example.barber_shop.Entity.ServiceType;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Exception.ServiceTypeNotFoundException;
import org.example.barber_shop.Mapper.ServiceMapper;
import org.example.barber_shop.Repository.FileRepository;
import org.example.barber_shop.Repository.ServiceRepository;
import org.example.barber_shop.Repository.ServiceTypeRepository;
import org.example.barber_shop.Repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final FileUploadService fileUploadService;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    public ServiceResponse addService(ServiceRequest serviceRequest) throws IOException {
        Service service = new Service();
        service.setName(serviceRequest.name);
        service.setDescription(serviceRequest.description);
        service.setPrice(serviceRequest.price);
        service.setEstimateTime(serviceRequest.estimateTime);
        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(serviceRequest.serviceTypeId);
        if (serviceTypeOptional.isPresent()) {
            ServiceType serviceType = serviceTypeOptional.get();
            service.setServiceType(serviceType);
            JsonNode[] jsonNodes = fileUploadService.multipleFileUploadImgBB(serviceRequest.images);
            List<File> files = new ArrayList<>();
            User user = userRepository.findById(SecurityUtils.getCurrentUserId()).get();
            for (JsonNode jsonNode : jsonNodes) {
                File file = new File();
                file.setName(jsonNode.path("data").path("image").path("name").asText());
                file.setUrl(jsonNode.path("data").path("url").asText());
                file.setThumbUrl(jsonNode.path("data").path("thumb").path("url").asText());
                file.setMediumUrl(jsonNode.path("data").path("medium").path("url").asText());
                file.setDeleteUrl(jsonNode.path("data").path("delete_url").asText());
                file.setOwner(user);
                File savedFile = fileRepository.save(file);
                files.add(savedFile);
            }
            service.setImages(files);
            Service savedService = serviceRepository.save(service);
            serviceTypeRepository.save(serviceType);
            return serviceMapper.toResponse(savedService);
        } else {
            throw new ServiceTypeNotFoundException("Invalid service type id, there is no service type with this id.");
        }
    }

    public List<ServiceResponse> getAllServices() {
        List<Service> services = serviceRepository.findAll();
        return serviceMapper.toResponseList(services);
    }
}
