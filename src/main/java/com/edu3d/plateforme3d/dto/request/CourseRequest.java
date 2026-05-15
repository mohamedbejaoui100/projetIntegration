package com.edu3d.plateforme3d.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 200, message = "Titre trop long")
        String title,

        String description    // ← pas de @NotBlank, pas de @Size
) {}