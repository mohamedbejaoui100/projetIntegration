package com.edu3d.plateforme3d.dto.request;

public record RegisterRequest(
        @NotBlank String nom,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotNull Role role
) {}
