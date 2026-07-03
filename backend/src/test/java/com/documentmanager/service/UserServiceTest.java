package com.documentmanager.service;

import com.documentmanager.dto.UserDto;
import com.documentmanager.exception.EmailAlreadyExistsException;
import com.documentmanager.mapper.UserMapper;
import com.documentmanager.model.User;
import com.documentmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserCreatesAndReturnsDto() {
        UserDto request = new UserDto();
        request.setEmail("test@example.com");
        request.setFullName("Test User");

        User entity = new User();
        entity.setEmail("test@example.com");
        entity.setFullName("Test User");

        User savedEntity = new User();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setEmail("test@example.com");
        savedEntity.setFullName("Test User");
        savedEntity.setCreatedAt(LocalDateTime.now());

        UserDto response = new UserDto();
        response.setId(savedEntity.getId());
        response.setEmail("test@example.com");
        response.setFullName("Test User");
        response.setCreatedAt(savedEntity.getCreatedAt());

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(response);

        UserDto result = userService.createUser(request);

        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getFullName());
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void createUserWithDuplicateEmailThrowsException() {
        UserDto request = new UserDto();
        request.setEmail("duplicate@example.com");

        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    void getAllUsersReturnsListOfDtos() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("alice@example.com");
        user1.setFullName("Alice");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("bob@example.com");
        user2.setFullName("Bob");

        UserDto dto1 = new UserDto();
        dto1.setId(user1.getId());
        dto1.setEmail("alice@example.com");
        dto1.setFullName("Alice");

        UserDto dto2 = new UserDto();
        dto2.setId(user2.getId());
        dto2.setEmail("bob@example.com");
        dto2.setFullName("Bob");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("alice@example.com", result.get(0).getEmail());
        assertEquals("bob@example.com", result.get(1).getEmail());
    }
}
