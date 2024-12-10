package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.Service.ServiceUpdateRequest;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.DTO.Service.ServiceRequest;
import org.example.barber_shop.DTO.Service.ServiceResponse;
import org.example.barber_shop.Mapper.ServiceMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final ComboRepository comboRepository;

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
            throw new LocalizedException("service.type.not.found");
        }
    }

    public List<ServiceResponse> getAllServices() {
        List<Service> services = serviceRepository.findByDeletedFalse();
        return serviceMapper.toResponseList(services);
    }
    public ServiceResponse updateService(ServiceUpdateRequest serviceUpdateRequest) throws IOException {
        Optional<Service> serviceOptional = serviceRepository.findById(serviceUpdateRequest.id);
        if (serviceOptional.isPresent()) {
            Service service = serviceOptional.get();
            if (!service.isDeleted()){
                Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(serviceUpdateRequest.serviceTypeId);
                if (serviceTypeOptional.isPresent() && !serviceOptional.get().isDeleted()) {
                    service.setName(serviceUpdateRequest.name);
                    service.setDescription(serviceUpdateRequest.description);
                    service.setPrice(serviceUpdateRequest.price);
                    service.setEstimateTime(serviceUpdateRequest.estimateTime);
                    service.setServiceType(serviceTypeOptional.get());
                    if (serviceUpdateRequest.remove_images != null) {
                        service.getImages().removeIf(image ->
                                serviceUpdateRequest.remove_images.contains(image.getId())
                        );
                    }
                    if (serviceUpdateRequest.new_images != null) {
                        JsonNode[] jsonNodes = fileUploadService.multipleFileUploadImgBB(serviceUpdateRequest.new_images);
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
                            service.getImages().add(savedFile);
                        }
                    }
                    return serviceMapper.toResponse(serviceRepository.save(service));
                } else {
                    throw new LocalizedException("service.type.not.found");
                }
            } else {
                throw new LocalizedException("service.not.found");
            }
        } else {
            throw new LocalizedException("service.not.found");
        }
    }
    public boolean delete(long id){
        Optional<Service> serviceOptional = serviceRepository.findById(id);
        if (serviceOptional.isPresent()){
            Service service = serviceOptional.get();
            service.setDeleted(true);
            service = serviceRepository.save(service);
            List<Combo> combos = comboRepository.findByDeletedFalse();
            for (Combo combo : combos) {
                for (Service serviceLoop : combo.getServices()) {
                    if (Objects.equals(serviceLoop.getId(), service.getId())){
                        combo.getServices().remove(serviceLoop);
                        break;
                    }
                }
            }
            comboRepository.saveAll(combos);
            return true;
        } else {
            return false;
        }
    }
}
