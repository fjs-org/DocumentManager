package com.documentmanager.service;

import com.documentmanager.dto.DocumentDto;

import java.util.List;

public interface DocumentService {

    DocumentDto createDocument(DocumentDto dto);

    List<DocumentDto> getAllDocuments();
}