package com.edu3d.plateforme3d.service;


import com.edu3d.plateforme3d.dto.request.CourseRequest;
import com.edu3d.plateforme3d.dto.response.CourseResponse;
import com.edu3d.plateforme3d.entity.*;
import com.edu3d.plateforme3d.exception.*;
import com.edu3d.plateforme3d.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseResponse createCourse(CourseRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable"));

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .teacher(teacher)
                .build();

        return mapToResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable : " + id));
    }

    private CourseResponse mapToResponse(Course c) {
        return new CourseResponse(
                c.getId(), c.getTitle(), c.getDescription(),
                c.getTeacher().getNom(), c.getSlides().size(), c.getCreatedAt()
        );
    }
}
