package br.edu.ufersa.SIPA.freatures.auth.service;

import br.edu.ufersa.SIPA.shared.exeception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controle de taxa (Rate Limiting) em janela deslizante e rastreamento de falhas para Captcha:
 * - Máximo 3 solicitações/hora por e-mail
 * - Máximo 10 solicitações/hora por endereço IP
 * - Exige desafio anti-bot (Captcha) após 3 tentativas falhas por IP ou conta
 */
@Service
public class PasswordRateLimiterService {

    private static final int MAX_REQUESTS_PER_EMAIL_PER_HOUR = 3;
    private static final int MAX_REQUESTS_PER_IP_PER_HOUR = 10;
    private static final int CAPTCHA_FAILURE_THRESHOLD = 3;
    private static final Duration WINDOW_DURATION = Duration.ofHours(1);

    private final Map<String, Deque<Instant>> emailRequests = new ConcurrentHashMap<>();
    private final Map<String, Deque<Instant>> ipRequests = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failureCountsByIp = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failureCountsByEmail = new ConcurrentHashMap<>();

    public void checkAndRecordForgotPasswordRateLimit(String email, String ip) {
        Instant now = Instant.now();

        if (ip != null && !ip.isBlank()) {
            if (!tryAcquire(ipRequests, ip.trim(), MAX_REQUESTS_PER_IP_PER_HOUR, now)) {
                throw new TooManyRequestsException(
                        "Limite de solicitações por endereço IP excedido (máx. 10/hora). Aguarde antes de tentar novamente.");
            }
        }

        if (email != null && !email.isBlank()) {
            String normalizedEmail = email.trim().toLowerCase();
            if (!tryAcquire(emailRequests, normalizedEmail, MAX_REQUESTS_PER_EMAIL_PER_HOUR, now)) {
                throw new TooManyRequestsException(
                        "Limite de solicitações para este e-mail excedido (máx. 3/hora). Aguarde antes de tentar novamente.");
            }
        }
    }

    public void recordFailedAttempt(String ip, String email) {
        if (ip != null && !ip.isBlank()) {
            failureCountsByIp.computeIfAbsent(ip.trim(), k -> new AtomicInteger(0)).incrementAndGet();
        }
        if (email != null && !email.isBlank()) {
            failureCountsByEmail.computeIfAbsent(email.trim().toLowerCase(), k -> new AtomicInteger(0)).incrementAndGet();
        }
    }

    public void resetFailedAttempts(String ip, String email) {
        if (ip != null && !ip.isBlank()) {
            failureCountsByIp.remove(ip.trim());
        }
        if (email != null && !email.isBlank()) {
            failureCountsByEmail.remove(email.trim().toLowerCase());
        }
    }

    public boolean isCaptchaRequired(String ip, String email) {
        int ipFailures = (ip != null && !ip.isBlank())
                ? failureCountsByIp.getOrDefault(ip.trim(), new AtomicInteger(0)).get()
                : 0;
        int emailFailures = (email != null && !email.isBlank())
                ? failureCountsByEmail.getOrDefault(email.trim().toLowerCase(), new AtomicInteger(0)).get()
                : 0;
        return ipFailures >= CAPTCHA_FAILURE_THRESHOLD || emailFailures >= CAPTCHA_FAILURE_THRESHOLD;
    }

    public int getFailureCount(String ip, String email) {
        int ipFailures = (ip != null && !ip.isBlank())
                ? failureCountsByIp.getOrDefault(ip.trim(), new AtomicInteger(0)).get()
                : 0;
        int emailFailures = (email != null && !email.isBlank())
                ? failureCountsByEmail.getOrDefault(email.trim().toLowerCase(), new AtomicInteger(0)).get()
                : 0;
        return Math.max(ipFailures, emailFailures);
    }

    private boolean tryAcquire(Map<String, Deque<Instant>> store, String key, int maxRequests, Instant now) {
        Deque<Instant> timestamps = store.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            Instant cutoff = now.minus(WINDOW_DURATION);
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxRequests) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }
}
