package com.edu3d.plateforme3d.dto.response;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id, String title, String description,
        String teacherName, int slideCount, LocalDateTime createdAt
) {}
