package br.edu.ufersa.SIPA.freatures.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "O token de recuperação é obrigatório.")
        String token,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, max = 128, message = "A nova senha deve ter no mínimo 6 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9]+$",
                message = "A senha deve conter apenas letras e números (ao menos uma letra e um número)."
        )
        String password,

        @NotBlank(message = "A confirmação da nova senha é obrigatória.")
        String passwordConfirm,

        @NotBlank(message = "O token de proteção CSRF é obrigatório.")
        String csrf,

        String captchaToken
) {}
