package com.documentmanager.service;

import com.documentmanager.dto.DocumentContentDto;
import com.documentmanager.dto.DocumentDto;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentDto createDocument(DocumentDto dto);

    List<DocumentDto> getAllDocuments();

    DocumentContentDto getDocumentById(UUID id);
}