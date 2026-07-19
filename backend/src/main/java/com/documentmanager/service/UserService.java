package com.documentmanager.service;

import com.documentmanager.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto dto);

    java.util.List<UserDto> getAllUsers();

    UserDto getUserById(java.util.UUID id);
}
