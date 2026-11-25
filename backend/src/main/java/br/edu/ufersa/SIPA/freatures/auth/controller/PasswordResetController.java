package br.edu.ufersa.SIPA.freatures.auth.controller;

import br.edu.ufersa.SIPA.freatures.auth.dto.ForgotPasswordRequestDTO;
import br.edu.ufersa.SIPA.freatures.auth.dto.ResetPasswordRequestDTO;
import br.edu.ufersa.SIPA.freatures.auth.dto.TokenValidationResponseDTO;
import br.edu.ufersa.SIPA.freatures.auth.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints públicos de recuperação de senha em 2 etapas (OWASP ASVS v4 §2.5):
 * 1. POST /SIPA/auth/forgot-password
 * 2. GET  /SIPA/auth/reset-password/{token}
 * 3. POST /SIPA/auth/reset-password
 */
@RestController
@RequestMapping({"/SIPA/auth", "/auth"})
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto,
            HttpServletRequest request) {

        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String origin = request.getHeader("Origin");

        Map<String, Object> result = passwordResetService.requestPasswordReset(
                dto.email(), ip, userAgent, origin);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reset-password/{token}")
    public ResponseEntity<TokenValidationResponseDTO> validateResetToken(
            @PathVariable("token") String token,
            HttpServletRequest request) {

        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        TokenValidationResponseDTO validation =
                passwordResetService.validateResetToken(token, ip, userAgent);

        if (!validation.valid()) {
            return ResponseEntity.badRequest().body(validation);
        }
        return ResponseEntity.ok(validation);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto,
            HttpServletRequest request) {

        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        Map<String, Object> result = passwordResetService.resetPassword(
                dto.token(),
                dto.password(),
                dto.passwordConfirm(),
                dto.csrf(),
                dto.captchaToken(),
                ip,
                userAgent
        );
        return ResponseEntity.ok(result);
    }

    public static String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
