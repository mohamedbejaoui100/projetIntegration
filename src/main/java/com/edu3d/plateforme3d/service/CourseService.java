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
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // ← Supprimer l'ancienne vérification de rôle
        // Spring Security s'en occupe déjà via SecurityConfig

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
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getTeacher().getNom(),
                c.getSlides() != null ? c.getSlides().size() : 0,  // ← null safe
                c.getCreatedAt()
        );
    }
    public CourseResponse updateCourse(Long id, CourseRequest request, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable : " + id));

        // Vérifier que c'est bien le teacher propriétaire ou un admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable"));

        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = course.getTeacher().getId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Vous ne pouvez modifier que vos propres cours");
        }

        course.setTitle(request.title());
        course.setDescription(request.description());

        return mapToResponse(courseRepository.save(course));
    }

    public void deleteCourse(Long id, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable : " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable"));

        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = course.getTeacher().getId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Vous ne pouvez supprimer que vos propres cours");
        }

        courseRepository.delete(course);
    }
}
