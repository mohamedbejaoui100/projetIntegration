// dto/request/UserUpdateRequest.java
package com.edu3d.plateforme3d.dto.request;

import com.edu3d.plateforme3d.entity.enums.Role;

public record UserUpdateRequest(
        String nom,       // optionnel
        String email,     // optionnel
        Role role         // optionnel
) {}
