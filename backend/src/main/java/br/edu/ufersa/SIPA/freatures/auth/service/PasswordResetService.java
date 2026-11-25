package br.edu.ufersa.SIPA.freatures.auth.service;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.auth.dto.TokenValidationResponseDTO;
import br.edu.ufersa.SIPA.freatures.auth.entity.AuditLog;
import br.edu.ufersa.SIPA.freatures.auth.entity.PasswordResetToken;
import br.edu.ufersa.SIPA.freatures.auth.repository.AuditLogRepository;
import br.edu.ufersa.SIPA.freatures.auth.repository.PasswordResetTokenRepository;
import br.edu.ufersa.SIPA.shared.exeception.TooManyRequestsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço de recuperação e redefinição segura de senha (OWASP ASVS v4 §2.5).
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32; // 256 bits de entropia
    private static final int EXPIRATION_MINUTES = 20;
    private static final String GENERIC_ANTI_ENUMERATION_MESSAGE =
            "Se o endereço de e-mail estiver cadastrado no sistema, enviamos um link seguro de recuperação (válido por 20 minutos).";

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final AuditLogRepository auditLogRepository;
    private final PwnedPasswordValidatorService passwordValidator;
    private final PasswordRateLimiterService rateLimiter;
    private final EmailNotificationService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.mail.sandbox-mode:true}")
    private boolean sandboxMode;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            AuditLogRepository auditLogRepository,
            PwnedPasswordValidatorService passwordValidator,
            PasswordRateLimiterService rateLimiter,
            EmailNotificationService emailService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordValidator = passwordValidator;
        this.rateLimiter = rateLimiter;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Etapa 1: Solicitação de recuperação por e-mail.
     * - Rate limiting: 3/hora por e-mail, 10/hora por IP.
     * - Anti-enumeração: retorna sempre a mesma mensagem genérica.
     * - Token CSPRNG de 32 bytes persistido apenas como hash SHA-256.
     */
    @Transactional
    public Map<String, Object> requestPasswordReset(String rawEmail, String ip, String userAgent, String frontendOrigin) {
        String email = rawEmail != null ? rawEmail.trim().toLowerCase() : "";

        try {
            rateLimiter.checkAndRecordForgotPasswordRateLimit(email, ip);
        } catch (TooManyRequestsException ex) {
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_REQUEST", email, ip, userAgent, "RATE_LIMITED", ex.getMessage()));
            throw ex;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", GENERIC_ANTI_ENUMERATION_MESSAGE);

        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            // Invalida tokens anteriores ainda abertos para este e-mail
            tokenRepository.invalidateAllActiveTokensForEmail(email, Instant.now());

            // Gera 32 bytes criptograficamente seguros (CSPRNG) para o token e para o CSRF pareado
            String rawToken = generateSecureToken(TOKEN_BYTES);
            String rawCsrf = generateSecureToken(TOKEN_BYTES);

            String tokenHash = sha256Hex(rawToken);
            String csrfHash = sha256Hex(rawCsrf);
            Instant expiresAt = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);

            PasswordResetToken entity = new PasswordResetToken(
                    email, tokenHash, csrfHash, expiresAt, ip, userAgent);
            tokenRepository.save(entity);

            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_REQUEST", email, ip, userAgent, "SUCCESS", null));

            EmailNotificationService.RenderedEmail rendered =
                    emailService.sendPasswordResetEmail(email, rawToken, ip, frontendOrigin);

            // Em ambiente local Docker sem servidor SMTP externo configurado, disponibiliza
            // o preview da caixa de saída local para permitir o teste completo do fluxo de 2 etapas.
            if (sandboxMode) {
                Map<String, Object> sandboxMail = new LinkedHashMap<>();
                sandboxMail.put("subject", rendered.subject());
                sandboxMail.put("resetLink", rendered.resetLink());
                sandboxMail.put("timestamp", rendered.timestampUtc());
                sandboxMail.put("expiresInMinutes", EXPIRATION_MINUTES);
                response.put("sandboxEmail", sandboxMail);
            }
        } else {
            // Simula tempo de hash SHA-256 para manter tempo constante e evitar enumeração por timing
            sha256Hex(generateSecureToken(TOKEN_BYTES));
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_REQUEST", email, ip, userAgent, "BLOCKED", "EMAIL_NOT_REGISTERED"));
        }

        return response;
    }

    /**
     * Etapa 2 (GET): Valida o token de recuperação em tempo constante e retorna o token CSRF pareado.
     */
    @Transactional
    public TokenValidationResponseDTO validateResetToken(String rawToken, String ip, String userAgent) {
        if (rawToken == null || rawToken.isBlank()) {
            rateLimiter.recordFailedAttempt(ip, null);
            return new TokenValidationResponseDTO(false, "Token de recuperação ausente ou inválido.", null, null, 0, rateLimiter.isCaptchaRequired(ip, null));
        }

        String incomingHash = sha256Hex(rawToken.trim());
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(incomingHash);

        if (tokenOpt.isEmpty()) {
            rateLimiter.recordFailedAttempt(ip, null);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_TOKEN_VALIDATE", "unknown", ip, userAgent, "FAILURE", "TOKEN_NOT_FOUND"));
            return new TokenValidationResponseDTO(false, "Token de recuperação inválido ou inexistente.", null, null, 0, rateLimiter.isCaptchaRequired(ip, null));
        }

        PasswordResetToken tokenEntity = tokenOpt.get();

        // Comparação em tempo constante (MessageDigest.isEqual)
        boolean constantTimeMatch = constantTimeEquals(tokenEntity.getTokenHash(), incomingHash);
        if (!constantTimeMatch || tokenEntity.isUsed() || tokenEntity.isExpired()) {
            rateLimiter.recordFailedAttempt(ip, tokenEntity.getUserEmail());
            String reason = tokenEntity.isUsed() ? "TOKEN_ALREADY_USED" : "TOKEN_EXPIRED";
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_TOKEN_VALIDATE", tokenEntity.getUserEmail(), ip, userAgent, "FAILURE", reason));
            return new TokenValidationResponseDTO(
                    false,
                    tokenEntity.isUsed()
                            ? "Este link de recuperação já foi utilizado. Solicite um novo link."
                            : "Este link de recuperação expirou (limite de 20 minutos). Solicite um novo link.",
                    null,
                    null,
                    0,
                    rateLimiter.isCaptchaRequired(ip, tokenEntity.getUserEmail())
            );
        }

        // Emite token CSRF vinculado ao hash do token de reset
        String derivedCsrf = deriveCsrfToken(tokenEntity.getTokenHash(), tokenEntity.getCsrfTokenHash());
        long remainingSeconds = Math.max(0, Duration.between(Instant.now(), tokenEntity.getExpiresAt()).getSeconds());

        return new TokenValidationResponseDTO(
                true,
                "Token válido.",
                derivedCsrf,
                maskEmail(tokenEntity.getUserEmail()),
                remainingSeconds,
                rateLimiter.isCaptchaRequired(ip, tokenEntity.getUserEmail())
        );
    }

    /**
     * Etapa 3 (POST): Efetiva a redefinição de senha via token de uso único + proteção CSRF.
     */
    @Transactional
    public Map<String, Object> resetPassword(
            String rawToken,
            String newPassword,
            String passwordConfirm,
            String csrfToken,
            String captchaToken,
            String ip,
            String userAgent) {

        if (rawToken == null || rawToken.isBlank()) {
            rateLimiter.recordFailedAttempt(ip, null);
            throw new IllegalArgumentException("Token de recuperação inválido.");
        }

        String incomingHash = sha256Hex(rawToken.trim());
        PasswordResetToken tokenEntity = tokenRepository.findByTokenHash(incomingHash)
                .orElseGet(() -> {
                    rateLimiter.recordFailedAttempt(ip, null);
                    auditLogRepository.save(new AuditLog(
                            "PASSWORD_RESET_CONFIRM", "unknown", ip, userAgent, "FAILURE", "INVALID_TOKEN"));
                    throw new IllegalArgumentException("Token de recuperação inválido ou expirado.");
                });

        String targetEmail = tokenEntity.getUserEmail();

        // Verifica exigência de Captcha após 3 tentativas falhas
        if (rateLimiter.isCaptchaRequired(ip, targetEmail)) {
            if (captchaToken == null || captchaToken.isBlank() || !"HUMAN_VERIFIED".equals(captchaToken.trim())) {
                auditLogRepository.save(new AuditLog(
                        "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "BLOCKED", "CAPTCHA_REQUIRED"));
                throw new IllegalArgumentException("Verificação anti-bot (Captcha) obrigatória após múltiplas tentativas falhas.");
            }
        }

        // Comparação do token em tempo constante
        if (!constantTimeEquals(tokenEntity.getTokenHash(), incomingHash)) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "TOKEN_MISMATCH"));
            throw new IllegalArgumentException("Token de recuperação inválido.");
        }

        if (tokenEntity.isUsed()) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "TOKEN_REUSED"));
            throw new IllegalArgumentException("Este token de recuperação já foi utilizado (uso único).");
        }

        if (tokenEntity.isExpired()) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "TOKEN_EXPIRED"));
            throw new IllegalArgumentException("Este token de recuperação expirou (validade de 20 minutos).");
        }

        // Validação CSRF em tempo constante
        String expectedCsrf = deriveCsrfToken(tokenEntity.getTokenHash(), tokenEntity.getCsrfTokenHash());
        if (csrfToken == null || !constantTimeEquals(expectedCsrf, csrfToken.trim())) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "CSRF_INVALID"));
            throw new IllegalArgumentException("Falha na verificação de segurança CSRF. Recarregue a página de recuperação.");
        }

        if (newPassword == null || !newPassword.equals(passwordConfirm)) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "PASSWORD_CONFIRM_MISMATCH"));
            throw new IllegalArgumentException("A confirmação de senha não coincide com a nova senha.");
        }

        // Valida força da senha (≥12 chars, 3 de 4 tipos, HaveIBeenPwned k-Anonymity)
        try {
            passwordValidator.validatePasswordStrength(newPassword, targetEmail);
        } catch (IllegalArgumentException ex) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "WEAK_OR_PWNED_PASSWORD"));
            throw ex;
        }

        Usuario usuario = usuarioRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("Conta de usuário não encontrada."));

        if (passwordEncoder.matches(newPassword, usuario.getSenha())) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_RESET_CONFIRM", targetEmail, ip, userAgent, "FAILURE", "SAME_AS_CURRENT_PASSWORD"));
            throw new IllegalArgumentException("A nova senha deve ser diferente da sua senha anterior.");
        }

        // 1. Atualiza o hash da senha
        usuario.setSenha(passwordEncoder.encode(newPassword));

        // 2. Invalida TODAS as sessões ativas do usuário incrementando token_version (OWASP ASVS 2.5.6)
        usuario.incrementTokenVersion();
        usuarioRepository.save(usuario);

        // 3. Marca o token como utilizado (uso único) e invalida quaisquer outros tokens do usuário
        tokenEntity.setUsed(true);
        tokenEntity.setUsedAt(Instant.now());
        tokenRepository.save(tokenEntity);
        tokenRepository.invalidateAllActiveTokensForEmail(targetEmail, Instant.now());

        // 4. Limpa contadores de falhas e registra auditoria de sucesso
        rateLimiter.resetFailedAttempts(ip, targetEmail);
        auditLogRepository.save(new AuditLog(
                "PASSWORD_RESET_SUCCESS", targetEmail, ip, userAgent, "SUCCESS", null));

        // 5. Dispara e-mail de confirmação da alteração com Data/Hora, IP e User-Agent
        EmailNotificationService.RenderedEmail confirmationAlert =
                emailService.sendPasswordChangedAlert(targetEmail, ip, userAgent);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Senha redefinida com sucesso! Todas as sessões anteriores foram encerradas por segurança.");
        result.put("sessionsInvalidated", true);
        result.put("securityAlert", Map.of(
                "timestamp", confirmationAlert.timestampUtc(),
                "ipAddress", ip != null ? ip : "desconhecido",
                "userAgent", userAgent != null ? userAgent : "Navegador Web"
        ));
        return result;
    }

    private String generateSecureToken(int numBytes) {
        byte[] bytes = new byte[numBytes];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível no runtime.", e);
        }
    }

    private String deriveCsrfToken(String tokenHash, String csrfHash) {
        return sha256Hex(tokenHash + ":" + csrfHash);
    }

    private boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) return false;
        byte[] a = expected.getBytes(StandardCharsets.UTF_8);
        byte[] b = actual.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@", 2);
        String local = parts[0];
        String maskedLocal = local.length() <= 2
                ? local.charAt(0) + "*"
                : local.substring(0, 2) + "***";
        return maskedLocal + "@" + parts[1];
    }
}
