package com.edu3d.plateforme3d.controller;

import com.edu3d.plateforme3d.entity.User;
import com.edu3d.plateforme3d.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Tous les users — réservé ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Un user par ID — réservé ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User introuvable"));
    }

    // Tous les teachers
    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getTeachers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("TEACHER"))
                .toList();
    }

    // Tous les students
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getStudents() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("STUDENT"))
                .toList();
    }
}