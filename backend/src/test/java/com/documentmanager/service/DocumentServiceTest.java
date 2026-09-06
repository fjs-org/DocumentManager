package com.documentmanager.service;

import com.documentmanager.dto.DocumentDto;
import com.documentmanager.exception.FileSizeExceededException;
import com.documentmanager.mapper.DocumentMapper;
import com.documentmanager.model.Document;
import com.documentmanager.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void createDocumentCreatesAndReturnsDto() {
        UUID id = UUID.randomUUID();

        DocumentDto request = new DocumentDto();
        request.setFileName("hello.txt");
        request.setData("file content".getBytes());

        Document entity = new Document();
        Document savedEntity = new Document();
        savedEntity.setId(id);
        savedEntity.setLocation("documents/" + id);
        savedEntity.setFileName("hello.txt");

        DocumentDto response = new DocumentDto();
        response.setId(id);
        response.setFileName("hello.txt");

        when(documentMapper.toEntity(request)).thenReturn(entity);
        when(documentRepository.save(entity)).thenReturn(savedEntity);
        when(documentMapper.toDto(savedEntity)).thenReturn(response);

        DocumentDto result = documentService.createDocument(request);

        assertEquals(id, result.getId());
        assertEquals("hello.txt", result.getFileName());
        assertNull(entity.getOwner());
        assertNull(entity.getId());
        assertNull(entity.getLocation());
        assertNull(entity.getCreatedAt());
    }

    @Test
    void getAllDocumentsReturnsListOfDtos() {
        UUID id = UUID.randomUUID();

        Document document = new Document();
        document.setId(id);
        document.setFileName("hello.txt");
        document.setCreatedAt(LocalDateTime.now());

        DocumentDto dto = new DocumentDto();
        dto.setId(id);
        dto.setFileName("hello.txt");
        dto.setCreatedAt(document.getCreatedAt());

        when(documentRepository.findAll()).thenReturn(List.of(document));
        when(documentMapper.toDto(document)).thenReturn(dto);

        List<DocumentDto> result = documentService.getAllDocuments();

        assertEquals(1, result.size());
        assertEquals(id, result.getFirst().getId());
        assertEquals("hello.txt", result.getFirst().getFileName());
    }

    @Test
    void createDocumentThrowsWhenFileSizeExceedsLimit() {
        DocumentDto request = new DocumentDto();
        request.setFileName("big.txt");
        request.setData(new byte[1_048_576 + 1]);

        assertThrows(FileSizeExceededException.class, () -> documentService.createDocument(request));
    }
}