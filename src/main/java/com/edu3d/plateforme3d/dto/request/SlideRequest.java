package com.edu3d.plateforme3d.dto.request;

public record SlideRequest(
        @NotBlank String title,
        String contentText,
        String object3dUrl,
        @NotNull @Min(0) Integer position,
        @NotNull Long courseId
) {}
