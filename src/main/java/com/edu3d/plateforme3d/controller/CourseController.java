package com.edu3d.plateforme3d.controller;

// CourseController.java
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Cours")
public class CourseController {

    private final CourseService courseService;

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
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long teacherId = ((CustomUserDetails) userDetails).getId();
        return courseService.createCourse(request, teacherId);
    }

    @GetMapping("/{id}/slides")
    public List<SlideResponse> getCourseSlides(@PathVariable Long id) {
        return slideService.getSlidesByCourse(id);
    }
}
