package br.edu.ufersa.SIPA.freatures.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String generateToken(Usuario user){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("API do SIPA")
                .withSubject(user.getId().toString())
                .withClaim("role", user.getRole().getRoleName())
                .withExpiresAt(Instant.now().plus(15,ChronoUnit.MINUTES))
                .sign(algorithm);
    }
    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("API do SIPA").build().verify(token).getSubject();

        } catch (JWTVerificationException exception){
            return null;
        }
    }
}
