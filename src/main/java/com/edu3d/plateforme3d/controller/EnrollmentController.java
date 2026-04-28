package com.edu3d.plateforme3d.controller;


import com.edu3d.plateforme3d.security.CustomUserDetails;
import com.edu3d.plateforme3d.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        enrollmentService.enrollStudent(userDetails.getId(), courseId);
        return ResponseEntity.status(201).body("Inscription réussie !");
    }
}

