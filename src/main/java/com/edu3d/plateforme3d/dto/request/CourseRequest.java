package com.edu3d.plateforme3d.dto.request;

import jakarta.validation.constraints.*;

public record CourseRequest(
        @NotBlank @Size(max = 200) String title,
        String description
) {}
