package com.documentmanager.controller;

import com.documentmanager.dto.DocumentDto;
import com.documentmanager.exception.FileSizeExceededException;
import com.documentmanager.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DocumentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private DocumentService documentService;

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
    void createDocumentReturns200() throws Exception {
        UUID id = UUID.randomUUID();

        DocumentDto response = new DocumentDto();
        response.setId(id);
        response.setFileName("hello.txt");
        response.setCreatedAt(LocalDateTime.now());

        when(documentService.createDocument(any())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes());
        MockMultipartFile metadataPart = new MockMultipartFile(
                "document", "", MediaType.APPLICATION_JSON_VALUE,
                "{\"fileName\":\"hello.txt\"}".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/documents")
                .file(file)
                .file(metadataPart))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(id.toString()))
               .andExpect(jsonPath("$.fileName").value("hello.txt"))
               .andExpect(jsonPath("$.createdAt").isNotEmpty())
               .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin")
    void createDocumentFileTooLargeReturns400() throws Exception {
        when(documentService.createDocument(any()))
                .thenThrow(new FileSizeExceededException());

        MockMultipartFile file = new MockMultipartFile(
                "file", "big.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());
        MockMultipartFile metadataPart = new MockMultipartFile(
                "document", "", MediaType.APPLICATION_JSON_VALUE,
                "{\"fileName\":\"big.txt\"}".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/documents")
                .file(file)
                .file(metadataPart))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.timestamp").isNotEmpty())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.error").value("Bad Request"))
               .andExpect(jsonPath("$.message").value("File size exceeds the maximum allowed size of 1 MB"))
               .andExpect(jsonPath("$.path").value("/documents"));
    }

    @Test
    @WithMockUser(username = "admin")
    void getDocumentsReturns200() throws Exception {
        UUID id = UUID.randomUUID();

        DocumentDto dto = new DocumentDto();
        dto.setId(id);
        dto.setFileName("hello.txt");
        dto.setCreatedAt(LocalDateTime.now());

        when(documentService.getAllDocuments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/documents"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(id.toString()))
               .andExpect(jsonPath("$[0].fileName").value("hello.txt"))
               .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
               .andExpect(jsonPath("$[0].data").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin")
    void createDocumentMissingFileNameReturns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes());
        MockMultipartFile metadataPart = new MockMultipartFile(
                "document", "", MediaType.APPLICATION_JSON_VALUE,
                "{}".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/documents")
                .file(file)
                .file(metadataPart))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}