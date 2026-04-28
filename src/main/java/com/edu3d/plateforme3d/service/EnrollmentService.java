package com.edu3d.plateforme3d.service;

import com.edu3d.plateforme3d.entity.*;
import com.edu3d.plateforme3d.exception.ResourceNotFoundException;
import com.edu3d.plateforme3d.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public void enrollStudent(Long studentId, Long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Étudiant déjà inscrit à ce cours");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        Enrollment enrollment = Enrollment.builder()
                .student(student).course(course).build();
        enrollmentRepository.save(enrollment);
    }
}
