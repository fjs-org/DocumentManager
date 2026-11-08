package com.documentmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DocumentContentDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
    private UUID id;

    @Schema(description = "Original file name of the uploaded document", example = "invoice.pdf")
    private String fileName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-07-02T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Binary content of the document", example = "Q29udGVudCBvZiB0aGUgZmlsZQ==")
    private byte[] data;

    @Schema(description = "Extracted text content of the document",
            example = "Content of the file")
    private String content;
}