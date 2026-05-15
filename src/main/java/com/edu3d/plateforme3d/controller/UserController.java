package com.edu3d.plateforme3d.controller;

import com.edu3d.plateforme3d.dto.request.UserUpdateRequest;
import com.edu3d.plateforme3d.entity.User;
import com.edu3d.plateforme3d.repository.UserRepository;
import com.edu3d.plateforme3d.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.edu3d.plateforme3d.exception.ResourceNotFoundException;
import com.edu3d.plateforme3d.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userService;  // ← ton service existant

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getTeachers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("TEACHER"))
                .toList();
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getStudents() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("STUDENT"))
                .toList();
    }

    // ✅ PATCH — modifier un ou plusieurs champs
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User patchUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request
    ) {
        return userService.patchUser(id, request);
    }

    // ✅ DELETE — supprimer un user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable"));
    }
}