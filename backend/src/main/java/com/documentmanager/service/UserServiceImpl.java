package com.documentmanager.service;

import com.documentmanager.dto.UserDto;
import com.documentmanager.exception.EmailAlreadyExistsException;
import com.documentmanager.exception.UserNotFoundException;
import com.documentmanager.mapper.UserMapper;
import com.documentmanager.model.User;
import com.documentmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public java.util.List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getUserById(java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Attempt to create user with existing email: {}", dto.getEmail());
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        log.info("Created user with id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }
}
