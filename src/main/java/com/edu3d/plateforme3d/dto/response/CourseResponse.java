package com.edu3d.plateforme3d.dto.response;

public record CourseResponse(
        Long id, String title, String description,
        String teacherName, int slideCount, LocalDateTime createdAt
) {}
