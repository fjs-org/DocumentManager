package com.documentmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DocumentDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
    private UUID id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "Q29udGVudCBvZiB0aGUgZmlsZQ==")
    private byte[] data;

    @NotBlank
    @Schema(description = "Original file name of the uploaded document", example = "invoice.pdf")
    private String fileName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-07-02T12:00:00")
    private LocalDateTime createdAt;
}