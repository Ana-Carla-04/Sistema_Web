package br.edu.ufersa.SIPA.freatures.auth.dto;

public record TokenValidationResponseDTO(
        boolean valid,
        String message,
        String csrfToken,
        String maskedEmail,
        long expiresInSeconds,
        boolean captchaRequired
) {}
