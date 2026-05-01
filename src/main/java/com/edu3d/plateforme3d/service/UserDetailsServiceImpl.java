package com.edu3d.plateforme3d.service;

import com.edu3d.plateforme3d.dto.request.UserUpdateRequest;
import com.edu3d.plateforme3d.entity.User;
import com.edu3d.plateforme3d.exception.ResourceNotFoundException;
import com.edu3d.plateforme3d.repository.UserRepository;
import com.edu3d.plateforme3d.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // ← déjà présent — ne pas toucher
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
    }

    // ← ajoute ces méthodes en dessous

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable : " + id));
    }

    @Transactional
    public User patchUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable : " + id));

        if (request.nom() != null)   user.setNom(request.nom());
        if (request.email() != null) user.setEmail(request.email());
        if (request.role() != null)  user.setRole(request.role());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable : " + id));
        userRepository.delete(user);
    }
}