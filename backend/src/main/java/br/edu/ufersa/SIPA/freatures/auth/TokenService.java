package br.edu.ufersa.SIPA.freatures.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private static final String ISSUER = "API do SIPA";

    @Value("${api.security.token.secret}")
    private String secret;

    @PostConstruct
    void validarConfiguracaoDeSegredo() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET não configurado no ambiente.");
        }
        if (secret.length() < 32) {
            log.warn("Atenção (OWASP A04): O segredo JWT possui menos de 32 caracteres. Recomenda-se no mínimo 256 bits.");
        }
    }

    public String generateToken(Usuario user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant agora = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(agora)
                .withSubject(user.getId().toString())
                .withClaim("role", user.getRole().getRoleName())
                .withClaim("tv", user.getTokenVersion())
                .withExpiresAt(agora.plus(180, ChronoUnit.MINUTES))
                .sign(algorithm);
    }

    public String validateToken(String token) {
        DecodedJWT decoded = verifyAndDecode(token);
        return decoded != null ? decoded.getSubject() : null;
    }

    public DecodedJWT verifyAndDecode(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
