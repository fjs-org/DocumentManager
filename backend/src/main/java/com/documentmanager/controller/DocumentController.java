package com.documentmanager.controller;

import com.documentmanager.dto.DocumentDto;
import com.documentmanager.exception.ErrorResponse;
import com.documentmanager.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Documents", description = "Document management endpoints")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    @Operation(summary = "Get all documents",
            description = "Returns metadata (id, fileName, ownerId, createdAt) of all documents without binary content")
    @ApiResponse(responseCode = "200", description = "List of document metadata retrieved successfully")
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document", description = "Uploads a document file with its metadata",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "document", contentType = MediaType.APPLICATION_JSON_VALUE),
                                    @Encoding(name = "file", contentType = "application/pdf, image/png, image/jpeg")
                            }
                    )
            )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid metadata, missing part, or file exceeds max size",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthenticated access",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DocumentDto> createDocument(
            @RequestPart("document") @Valid DocumentDto dto,
            @RequestPart("file") MultipartFile file) throws IOException

    {
        dto.setData(file.getBytes());
        DocumentDto response = documentService.createDocument(dto);
        return ResponseEntity.ok(response);
    }
}


