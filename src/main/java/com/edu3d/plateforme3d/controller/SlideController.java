package com.edu3d.plateforme3d.controller;

import com.edu3d.plateforme3d.dto.request.SlideRequest;
import com.edu3d.plateforme3d.dto.response.SlideResponse;
import com.edu3d.plateforme3d.service.SlideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slides")
@RequiredArgsConstructor
public class SlideController {

    private final SlideService slideService;

    // ✅ POST créer une slide
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SlideResponse createSlide(@Valid @RequestBody SlideRequest request) {
        return slideService.createSlide(request);
    }

    // ✅ GET une slide par ID
    @GetMapping("/{id}")
    public SlideResponse getSlide(@PathVariable Long id) {
        return slideService.getSlideById(id);
    }

    // ✅ PUT modifier une slide
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public SlideResponse updateSlide(
            @PathVariable Long id,
            @Valid @RequestBody SlideRequest request
    ) {
        return slideService.updateSlide(id, request);
    }

    // ✅ DELETE supprimer une slide
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlide(@PathVariable Long id) {
        slideService.deleteSlide(id);
    }
}