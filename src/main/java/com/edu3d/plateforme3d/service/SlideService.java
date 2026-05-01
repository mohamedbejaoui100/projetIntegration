package com.edu3d.plateforme3d.service;

import com.edu3d.plateforme3d.dto.request.SlideRequest;
import com.edu3d.plateforme3d.dto.response.SlideResponse;
import com.edu3d.plateforme3d.entity.*;
import com.edu3d.plateforme3d.exception.ResourceNotFoundException;
import com.edu3d.plateforme3d.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SlideService {

    private final SlideRepository slideRepository;
    private final CourseRepository courseRepository;

    public SlideResponse createSlide(SlideRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        Slide slide = Slide.builder()
                .title(request.title())
                .contentText(request.contentText())
                .object3dUrl(request.object3dUrl())
                .position(request.position())
                .course(course)
                .build();

        return mapToResponse(slideRepository.save(slide));
    }

    @Transactional(readOnly = true)
    public List<SlideResponse> getSlidesByCourse(Long courseId) {
        return slideRepository.findByCourseIdOrderByPositionAsc(courseId)
                .stream().map(this::mapToResponse).toList();
    }

    private SlideResponse mapToResponse(Slide s) {
        return new SlideResponse(s.getId(), s.getTitle(),
                s.getContentText(), s.getObject3dUrl(), s.getPosition());
    }
    @Transactional(readOnly = true)
    public SlideResponse getSlideById(Long id) {
        Slide slide = slideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slide introuvable : " + id));
        return mapToResponse(slide);
    }

    public SlideResponse updateSlide(Long id, SlideRequest request) {
        Slide slide = slideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slide introuvable : " + id));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        slide.setTitle(request.title());
        slide.setContentText(request.contentText());
        slide.setObject3dUrl(request.object3dUrl());
        slide.setPosition(request.position());
        slide.setCourse(course);

        return mapToResponse(slideRepository.save(slide));
    }

    public void deleteSlide(Long id) {
        Slide slide = slideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slide introuvable : " + id));
        slideRepository.delete(slide);
    }
}
