package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.User.UserResponse;
import org.example.barber_shop.DTO.User.RegisterRequest;
import org.example.barber_shop.DTO.User.UserResponseNoFile;
import org.example.barber_shop.Entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
    UserResponse toResponse(User user);
    List<UserResponse> toResponses(List<User> users);
    UserResponseNoFile toResponseNoFile(User user);
}
