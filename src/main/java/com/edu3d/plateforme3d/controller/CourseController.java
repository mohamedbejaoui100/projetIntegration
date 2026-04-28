package com.edu3d.plateforme3d.controller;

import com.edu3d.plateforme3d.dto.request.CourseRequest;
import com.edu3d.plateforme3d.dto.response.*;
import com.edu3d.plateforme3d.security.CustomUserDetails;
import com.edu3d.plateforme3d.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final SlideService slideService;

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return courseService.createCourse(request, userDetails.getId());
    }

    @GetMapping("/{id}/slides")
    public List<SlideResponse> getCourseSlides(@PathVariable Long id) {
        return slideService.getSlidesByCourse(id);
    }
}