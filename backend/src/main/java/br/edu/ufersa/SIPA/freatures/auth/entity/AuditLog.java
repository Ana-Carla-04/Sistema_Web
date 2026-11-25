package br.edu.ufersa.SIPA.freatures.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Registro imutável de auditoria para eventos críticos de segurança e credenciais.
 * Jamais armazena senhas, tokens puros ou hashes completos.
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_logs_event_date", columnList = "event_type, created_at"),
                @Index(name = "idx_audit_logs_ip", columnList = "ip_address, created_at"),
                @Index(name = "idx_audit_logs_target", columnList = "target_identifier, created_at")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(name = "target_identifier", nullable = false, length = 150)
    private String targetIdentifier;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(nullable = false, length = 25)
    private String status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public AuditLog() {}

    public AuditLog(
            String eventType,
            String targetIdentifier,
            String ipAddress,
            String userAgent,
            String status,
            String failureReason) {
        this.eventType = eventType;
        this.targetIdentifier = targetIdentifier != null ? targetIdentifier : "anonymous";
        this.ipAddress = ipAddress != null ? ipAddress : "0.0.0.0";
        this.userAgent = userAgent != null && userAgent.length() > 500
                ? userAgent.substring(0, 500)
                : userAgent;
        this.status = status;
        this.failureReason = failureReason;
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getTargetIdentifier() { return targetIdentifier; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getStatus() { return status; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
}
