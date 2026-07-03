package com.documentmanager.controller;

import com.documentmanager.dto.UserDto;
import com.documentmanager.exception.EmailAlreadyExistsException;
import com.documentmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin")
    void getUsersReturns200() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(UUID.randomUUID());
        dto.setEmail("alice@example.com");
        dto.setFullName("Alice");
        dto.setCreatedAt(LocalDateTime.now());

        when(userService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/users"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].email").value("alice@example.com"))
               .andExpect(jsonPath("$[0].fullName").value("Alice"));
    }

    @Test
    @WithMockUser(username = "admin")
    void createUserReturns211() throws Exception {
        UserDto request = new UserDto();
        request.setEmail("test@example.com");
        request.setFullName("Test User");

        UserDto response = new UserDto();
        response.setId(UUID.randomUUID());
        response.setEmail("test@example.com");
        response.setFullName("Test User");
        response.setCreatedAt(LocalDateTime.now());

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().is(211))
               .andExpect(jsonPath("$.email").value("test@example.com"))
               .andExpect(jsonPath("$.fullName").value("Test User"))
               .andExpect(jsonPath("$.id").isNotEmpty())
               .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void createUserWithDuplicateEmailReturns400() throws Exception {
        UserDto request = new UserDto();
        request.setEmail("duplicate@example.com");
        request.setFullName("Test");

        when(userService.createUser(any())).thenThrow(new EmailAlreadyExistsException("duplicate@example.com"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.timestamp").isNotEmpty())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.error").value("Bad Request"))
               .andExpect(jsonPath("$.message").value("Email already exists: duplicate@example.com"))
               .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void createUserRequiresAuth() throws Exception {
        UserDto request = new UserDto();
        request.setEmail("unauth@example.com");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized());
    }
}
