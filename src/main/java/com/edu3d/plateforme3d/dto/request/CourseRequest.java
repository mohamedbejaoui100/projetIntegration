package com.edu3d.plateforme3d.dto.request;

public record CourseRequest(
        @NotBlank @Size(max = 200) String title,
        String description
) {}
