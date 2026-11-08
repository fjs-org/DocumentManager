package com.documentmanager.service;

import com.documentmanager.dto.DocumentContentDto;
import com.documentmanager.dto.DocumentDto;
import com.documentmanager.exception.DocumentNotFoundException;
import com.documentmanager.exception.FileSizeExceededException;
import com.documentmanager.mapper.DocumentMapper;
import com.documentmanager.model.Document;
import com.documentmanager.repository.DocumentRepository;
import com.documentmanager.util.DocumentContentExtractor;
import com.documentmanager.util.ImageContentExtractor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L;

    private static final String PDF_EXTENSION = ".pdf";

    private static final String JPG_EXTENSION = ".jpg";

    private static final String JPEG_EXTENSION = ".jpeg";

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentContentExtractor documentContentExtractor;
    private final ImageContentExtractor imageContentExtractor;

    @Override
    public DocumentDto createDocument(DocumentDto dto) {
        if (dto.getData() == null || dto.getData().length > MAX_FILE_SIZE_BYTES) {
            throw new FileSizeExceededException();
        }

        Document document = documentMapper.toEntity(dto);
        document.setFileName(dto.getFileName());
        document.setData(dto.getData());
        document.setOwner(null);

        Document savedDocument = documentRepository.save(document);
        log.info("Created document with id: {}", savedDocument.getId());
        return documentMapper.toDto(savedDocument);
    }

    @Override
    public List<DocumentDto> getAllDocuments() {
        List<Document> documents = documentRepository.findAll();
        log.info("Retrieved {} documents", documents.size());
        return documents.stream()
                .map(documentMapper::toDto)
                .toList();
    }

    @Override
    public DocumentContentDto getDocumentById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        log.info("Retrieved document with id: {}", document.getId());

        DocumentContentDto dto = documentMapper.toContentDto(document);
        if (isPdf(document.getFileName())) {
            dto.setContent(documentContentExtractor.extractText(document.getData()));
        } else if (isJpg(document.getFileName())) {
            dto.setContent(imageContentExtractor.extractText(document.getData()));
        }
        return dto;
    }

    private boolean isPdf(String fileName) {
        return hasExtension(fileName, PDF_EXTENSION);
    }

    private boolean isJpg(String fileName) {
        return hasExtension(fileName, JPG_EXTENSION) || hasExtension(fileName, JPEG_EXTENSION);
    }

    private boolean hasExtension(String fileName, String extension) {
        return fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(extension);
    }
}