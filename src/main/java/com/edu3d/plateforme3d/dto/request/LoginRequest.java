package com.edu3d.plateforme3d.dto.request;

public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
