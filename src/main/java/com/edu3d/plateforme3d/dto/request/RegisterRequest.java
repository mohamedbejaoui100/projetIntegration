package com.edu3d.plateforme3d.dto.request;

import com.edu3d.plateforme3d.entity.enums.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @Email(message = "Email invalide") @NotBlank String email,
        @NotBlank @Size(min = 8, message = "Mot de passe minimum 8 caractères") String password,
        @NotNull Role role
) {}

