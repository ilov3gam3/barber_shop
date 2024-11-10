package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Combo.ComboResponse;
import org.example.barber_shop.Entity.Combo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComboMapper {
    ComboResponse toResponse(Combo combo);
    List<ComboResponse> toResponses(List<Combo> combos);
}
