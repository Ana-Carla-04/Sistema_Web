package br.edu.ufersa.SIPA.freatures.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entidade que armazena tokens de recuperação de senha (OWASP ASVS v4 §2.5).
 * O token puro NUNCA é salvo no banco; armazena-se exclusivamente o hash SHA-256 (64 chars hex).
 */
@Entity
@Table(
        name = "password_reset_tokens",
        indexes = {
                @Index(name = "idx_reset_token_hash", columnList = "token_hash", unique = true),
                @Index(name = "idx_reset_token_email_expires", columnList = "user_email, expires_at, used")
        }
)
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false, length = 150)
    private String userEmail;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Column(name = "csrf_token_hash", nullable = false, length = 64)
    private String csrfTokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "requested_ip", nullable = false, length = 45)
    private String requestedIp;

    @Column(name = "requested_user_agent", length = 500)
    private String requestedUserAgent;

    public PasswordResetToken() {}

    public PasswordResetToken(
            String userEmail,
            String tokenHash,
            String csrfTokenHash,
            Instant expiresAt,
            String requestedIp,
            String requestedUserAgent) {
        this.userEmail = userEmail;
        this.tokenHash = tokenHash;
        this.csrfTokenHash = csrfTokenHash;
        this.expiresAt = expiresAt;
        this.requestedIp = requestedIp;
        this.requestedUserAgent = requestedUserAgent;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getTokenHash() { return tokenHash; }
    public String getCsrfTokenHash() { return csrfTokenHash; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUsedAt() { return usedAt; }
    public boolean isUsed() { return used; }
    public String getRequestedIp() { return requestedIp; }
    public String getRequestedUserAgent() { return requestedUserAgent; }

    public void setUsed(boolean used) { this.used = used; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }
}
