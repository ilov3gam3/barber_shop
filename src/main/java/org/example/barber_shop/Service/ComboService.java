package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.Combo.ComboUpdateRequest;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.DTO.Combo.ComboRequest;
import org.example.barber_shop.DTO.Combo.ComboResponse;
import org.example.barber_shop.Entity.Combo;
import org.example.barber_shop.Entity.File;
import org.example.barber_shop.Entity.Service;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Exception.ServiceNotFoundException;
import org.example.barber_shop.Mapper.ComboMapper;
import org.example.barber_shop.Repository.ComboRepository;
import org.example.barber_shop.Repository.FileRepository;
import org.example.barber_shop.Repository.ServiceRepository;
import org.example.barber_shop.Repository.UserRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ComboService {
    private final ComboRepository comboRepository;
    private final ComboMapper comboMapper;
    private final ServiceRepository serviceRepository;
    private final FileUploadService fileUploadService;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    public ComboResponse addCombo(ComboRequest comboRequest) throws IOException {
        List<Service> services = serviceRepository.findAllById(comboRequest.serviceIds);
        if (!services.isEmpty()){
            Combo combo = new Combo();
            combo.setName(comboRequest.name);
            combo.setDescription(comboRequest.description);
            combo.setPrice(comboRequest.price);
            combo.setEstimateTime(comboRequest.estimateTime);
            combo.setServices(services);
            JsonNode[] jsonNodes = fileUploadService.multipleFileUploadImgBB(comboRequest.images);
            User user = userRepository.findById(SecurityUtils.getCurrentUserId()).get();
            List<File> files = new ArrayList<>();
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
            combo.setImages(files);
            Combo savedCombo = comboRepository.save(combo);
            return comboMapper.toResponse(savedCombo);
        } else {
            throw new ServiceNotFoundException("Invalid ids, no service is found with provided ids");
        }
    }

    public List<ComboResponse> getAllCombo(){
        List<Combo> combos = comboRepository.findByDeletedFalse();
        return comboMapper.toResponses(combos);
    }
    public boolean delete(long id){
        Optional<Combo> comboOptional = comboRepository.findById(id);
        if (comboOptional.isPresent()){
            Combo combo = comboOptional.get();
            combo.setDeleted(true);
            comboRepository.save(combo);
            return true;
        } else {
            return false;
        }
    }
    public ComboResponse update(ComboUpdateRequest comboUpdateRequest) throws IOException {
        Optional<Combo> comboOptional = comboRepository.findById(comboUpdateRequest.id);
        if (comboOptional.isPresent()){
            Combo combo = comboOptional.get();
            List<Service> newServices = serviceRepository.findAllByIdInAndDeletedFalse(comboUpdateRequest.serviceIds);
            //
            List<Service> currentServices = combo.getServices();
            Set<Long> newServiceIds = newServices.stream()
                    .map(Service::getId) // Assuming Service has a getId() method
                    .collect(Collectors.toSet());
            currentServices.removeIf(service -> !newServiceIds.contains(service.getId()));
            for (Service newService : newServices) {
                boolean alreadyExists = currentServices.stream()
                        .anyMatch(service -> service.getId().equals(newService.getId()));
                if (!alreadyExists) {
                    currentServices.add(newService);
                }
            }
            combo.setServices(currentServices);
            //
            if (comboUpdateRequest.remove_images != null){
                combo.getImages().removeIf(image -> comboUpdateRequest.remove_images.contains(image.getId()));
            }
            if (comboUpdateRequest.new_images != null){
                JsonNode[] jsonNodes = fileUploadService.multipleFileUploadImgBB(comboUpdateRequest.new_images);
                User user = SecurityUtils.getCurrentUser();
                for (JsonNode jsonNode : jsonNodes) {
                    File file = new File();
                    file.setName(jsonNode.path("data").path("image").path("name").asText());
                    file.setUrl(jsonNode.path("data").path("url").asText());
                    file.setThumbUrl(jsonNode.path("data").path("thumb").path("url").asText());
                    file.setMediumUrl(jsonNode.path("data").path("medium").path("url").asText());
                    file.setDeleteUrl(jsonNode.path("data").path("delete_url").asText());
                    file.setOwner(user);
                    File savedFile = fileRepository.save(file);
                    combo.getImages().add(savedFile);
                }
            }
            return comboMapper.toResponse(comboRepository.save(combo));
        } else {
            throw new RuntimeException("Combo id not found");
        }
    }
}
