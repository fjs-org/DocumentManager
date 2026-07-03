package com.documentmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @NotBlank
    @Email
    @Schema(example = "john.doe@example.com")
    private String email;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "John Doe")
    private String fullName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-07-02T12:00:00")
    private LocalDateTime createdAt;
}
