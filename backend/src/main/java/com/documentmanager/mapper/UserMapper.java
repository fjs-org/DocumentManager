package com.documentmanager.mapper;

import com.documentmanager.dto.UserDto;
import com.documentmanager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "documents", ignore = true)
    User toEntity(UserDto dto);

    UserDto toDto(User user);
}
