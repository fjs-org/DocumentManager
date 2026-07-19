package com.documentmanager.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void swaggerUiIsPublic() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
               .andExpect(status().isFound());
    }

    @Test
    void swaggerApiDocsIsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
               .andExpect(status().isOk());
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/health"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void unauthenticatedAccessToProtectedEndpointReturns403WithErrorResponse() throws Exception {
        mockMvc.perform(get("/users"))
               .andExpect(status().isForbidden())
               .andExpect(jsonPath("$.timestamp").isNotEmpty())
               .andExpect(jsonPath("$.status").value(403))
               .andExpect(jsonPath("$.error").value("Forbidden"))
               .andExpect(jsonPath("$.message").value("Access Denied"))
               .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    @WithMockUser(username = "admin")
    void authenticatedAccessToProtectedEndpointSucceeds() throws Exception {
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
}
