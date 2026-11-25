package br.edu.ufersa.SIPA.freatures.auth.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Validador de senha simplificado:
 * - Mínimo de 6 caracteres
 * - Apenas letras e números (ao menos uma letra e um número)
 */
@Service
public class PwnedPasswordValidatorService {

    private static final Pattern HAS_LETTER = Pattern.compile("[A-Za-z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("[0-9]");
    private static final Pattern ONLY_ALPHANUMERIC = Pattern.compile("^[A-Za-z0-9]+$");

    public void validatePasswordStrength(String password, String userEmail) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "A senha deve ter no mínimo 6 caracteres.");
        }

        if (password.length() > 128) {
            throw new IllegalArgumentException(
                    "A senha não pode exceder 128 caracteres.");
        }

        if (!ONLY_ALPHANUMERIC.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "A senha deve conter apenas letras e números.");
        }

        if (!HAS_LETTER.matcher(password).find() || !HAS_DIGIT.matcher(password).find()) {
            throw new IllegalArgumentException(
                    "A senha deve conter pelo menos uma letra e um número.");
        }
    }
}
