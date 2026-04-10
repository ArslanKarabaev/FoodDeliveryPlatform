package com.fooddelivery.auth_service.Mapper;

import com.fooddelivery.auth_service.Dto.RegisterRequest;
import com.fooddelivery.auth_service.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "cafeId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);
}