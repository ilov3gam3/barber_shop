package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.File.FileResponse;
import org.example.barber_shop.DTO.File.FileResponseNoOwner;
import org.example.barber_shop.Entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {
    @Mapping(source = "owner", target = "owner")
    FileResponse toFileResponse(File file);

    FileResponseNoOwner toFileResponseNoOwner(File file);
}
