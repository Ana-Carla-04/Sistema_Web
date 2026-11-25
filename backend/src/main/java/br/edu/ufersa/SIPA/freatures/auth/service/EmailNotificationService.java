package br.edu.ufersa.SIPA.freatures.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Serviço de notificações de segurança por e-mail (HTML + Texto Plano).
 * Em conformidade com OWASP ASVS, nunca expõe tokens puros nos logs de aplicação.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss 'UTC'").withZone(ZoneOffset.UTC);

    public record RenderedEmail(
            String to,
            String subject,
            String textBody,
            String htmlBody,
            String resetLink,
            String timestampUtc
    ) {}

    public RenderedEmail sendPasswordResetEmail(String recipientEmail, String rawToken, String ipAddress, String frontendOrigin) {
        String baseOrigin = (frontendOrigin != null && !frontendOrigin.isBlank())
                ? frontendOrigin.replaceAll("/+$", "")
                : "http://localhost:3001";

        // Link seguro utilizando fragmento (#token=...) para que o token nunca seja enviado em headers Referer
        String resetLink = baseOrigin + "/recuperar-senha#token=" + rawToken;
        String timestamp = UTC_FORMATTER.format(Instant.now());
        String safeIp = ipAddress != null ? ipAddress : "desconhecido";

        String htmlTemplate = loadTemplate("templates/email/password-reset.html");
        String textTemplate = loadTemplate("templates/email/password-reset.txt");

        String htmlBody = htmlTemplate
                .replace("{{resetLink}}", resetLink)
                .replace("{{timestamp}}", timestamp)
                .replace("{{ipAddress}}", safeIp);

        String textBody = textTemplate
                .replace("{{resetLink}}", resetLink)
                .replace("{{timestamp}}", timestamp)
                .replace("{{ipAddress}}", safeIp);

        // NUNCA loga o token puro no console/arquivo de log
        log.info("Notificação de recuperação de senha preparada para destinatário={} (IP={}, data={})",
                maskEmail(recipientEmail), safeIp, timestamp);

        return new RenderedEmail(
                recipientEmail,
                "SIPA - Recuperação de Senha (Expira em 20 minutos)",
                textBody,
                htmlBody,
                resetLink,
                timestamp
        );
    }

    public RenderedEmail sendPasswordChangedAlert(String recipientEmail, String ipAddress, String userAgent) {
        String timestamp = UTC_FORMATTER.format(Instant.now());
        String safeIp = ipAddress != null ? ipAddress : "desconhecido";
        String safeUa = (userAgent != null && !userAgent.isBlank()) ? userAgent : "Navegador/Dispositivo não identificado";

        String htmlTemplate = loadTemplate("templates/email/password-changed-alert.html");
        String textTemplate = loadTemplate("templates/email/password-changed-alert.txt");

        String htmlBody = htmlTemplate
                .replace("{{timestamp}}", timestamp)
                .replace("{{ipAddress}}", safeIp)
                .replace("{{userAgent}}", escapeHtml(safeUa));

        String textBody = textTemplate
                .replace("{{timestamp}}", timestamp)
                .replace("{{ipAddress}}", safeIp)
                .replace("{{userAgent}}", safeUa);

        log.info("Alerta de alteração de senha disparado para destinatário={} (IP={}, UA={}, data={})",
                maskEmail(recipientEmail), safeIp, safeUa, timestamp);

        return new RenderedEmail(
                recipientEmail,
                "SIPA - Alerta de Segurança: Sua senha foi alterada",
                textBody,
                htmlBody,
                null,
                timestamp
        );
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Template de e-mail {} não pôde ser lido, usando fallback.", path);
            return "SIPA Notificação de Segurança - Data: {{timestamp}} | IP: {{ipAddress}}";
        }
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

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
