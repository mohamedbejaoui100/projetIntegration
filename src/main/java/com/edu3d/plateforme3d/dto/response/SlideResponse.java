package com.edu3d.plateforme3d.dto.response;

public record SlideResponse(
        Long id, String title, String contentText,
        String object3dUrl, Integer position
) {}