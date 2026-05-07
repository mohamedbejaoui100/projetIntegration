package com.edu3d.plateforme3d.controller;

import com.edu3d.plateforme3d.entity.ClassRoom;
import com.edu3d.plateforme3d.entity.User;
import com.edu3d.plateforme3d.exception.ResourceNotFoundException;
import com.edu3d.plateforme3d.repository.ClassRoomRepository;
import com.edu3d.plateforme3d.repository.CourseRepository;
import com.edu3d.plateforme3d.repository.EnrollmentRepository;
import com.edu3d.plateforme3d.repository.UserRepository;
import com.edu3d.plateforme3d.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomRepository classRoomRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    // GET toutes les classes
    @GetMapping
    public List<ClassRoom> getAllClassrooms() {
        return classRoomRepository.findAll();
    }

    // GET classes du teacher connecté
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public List<ClassRoom> getMyClassrooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return classRoomRepository.findByCourseTeacherId(userDetails.getId());
    }

    // GET une classe par code d'invitation
    @GetMapping("/code/{code}")
    public ClassRoom getByInviteCode(@PathVariable String code) {
        return classRoomRepository.findByInviteCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Code invalide"));
    }

    // GET une classe par ID
    @GetMapping("/{id}")
    public ClassRoom getClassroom(@PathVariable Long id) {
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom introuvable"));
    }

    // GET étudiants d'une classe
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public List<User> getStudentsOfClassroom(@PathVariable Long id) {
        ClassRoom cls = classRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom introuvable"));
        if (cls.getCourse() == null) return List.of();
        return enrollmentRepository.findByCourseId(cls.getCourse().getId())
                .stream().map(e -> e.getStudent()).toList();
    }

    // POST créer une classe
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ClassRoom createClassroom(@RequestBody ClassRoom classroom) {
        return classRoomRepository.save(classroom);
    }

    // POST rejoindre par code
    @PostMapping("/join")
    @PreAuthorize("hasRole('STUDENT')")
    public Map<String, String> joinByCode(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String code = body.get("code");
        ClassRoom cls = classRoomRepository.findByInviteCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Code invalide"));

        if (cls.getCourse() != null) {
            boolean exists = enrollmentRepository
                    .existsByStudentIdAndCourseId(userDetails.getId(), cls.getCourse().getId());
            if (!exists) {
                com.edu3d.plateforme3d.entity.Enrollment enrollment =
                        new com.edu3d.plateforme3d.entity.Enrollment();
                enrollment.setStudent(userRepository.findById(userDetails.getId()).orElseThrow());
                enrollment.setCourse(cls.getCourse());
                enrollmentRepository.save(enrollment);
            }
        }
        return Map.of("message", "Inscrit avec succès !", "classroom", cls.getName());
    }

    // PUT modifier
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ClassRoom updateClassroom(@PathVariable Long id, @RequestBody ClassRoom updated) {
        ClassRoom c = classRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom introuvable"));
        c.setName(updated.getName());
        return classRoomRepository.save(c);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClassroom(@PathVariable Long id) {
        classRoomRepository.deleteById(id);
    }
}