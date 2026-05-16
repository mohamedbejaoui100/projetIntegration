package com.edu3d.plateforme3d.controller;

import com.edu3d.plateforme3d.dto.request.CourseRequest;
import com.edu3d.plateforme3d.dto.response.CourseResponse;
import com.edu3d.plateforme3d.dto.response.SlideResponse;
import com.edu3d.plateforme3d.security.CustomUserDetails;
import com.edu3d.plateforme3d.service.CourseService;
import com.edu3d.plateforme3d.service.SlideService;
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

    // GET cours auxquels l'étudiant est inscrit
    @GetMapping("/my-enrolled")
    public List<CourseResponse> getMyEnrolledCourses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return courseService.getEnrolledCourses(userDetails.getId());
    }


    @GetMapping("/{id}/slides")
    public List<SlideResponse> getCourseSlides(@PathVariable Long id) {
        return slideService.getSlidesByCourse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return courseService.createCourse(request, userDetails.getId());
    }
    @PutMapping("/{id}")
    public CourseResponse updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return courseService.updateCourse(id, request, userDetails.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        courseService.deleteCourse(id, userDetails.getId());
    }
}