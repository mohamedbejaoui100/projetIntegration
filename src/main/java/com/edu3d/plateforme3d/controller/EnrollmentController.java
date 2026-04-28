package com.edu3d.plateforme3d.controller;


@RestController
@RequestMapping("/api/enroll")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> enroll(
            @RequestParam Long courseId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long studentId = ((CustomUserDetails) userDetails).getId();
        enrollmentService.enrollStudent(studentId, courseId);
        return ResponseEntity.status(201).body("Inscription réussie !");
    }
}
