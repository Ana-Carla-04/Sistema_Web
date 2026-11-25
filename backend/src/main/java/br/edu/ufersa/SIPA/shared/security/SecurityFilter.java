package br.edu.ufersa.SIPA.shared.security;

import br.edu.ufersa.SIPA.freatures.auth.TokenService;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authorization.substring(7).trim();
            var decodedJwt = tokenService.verifyAndDecode(token);

            if (decodedJwt != null && decodedJwt.getSubject() != null) {
                try {
                    Long usuarioId = Long.valueOf(decodedJwt.getSubject());
                    Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

                    if (usuario != null) {
                        Integer tokenVersionClaim = decodedJwt.getClaim("tv").asInt();
                        int currentVersion = usuario.getTokenVersion();

                        // Se o token_version do banco foi incrementado (pós-reset ou troca de senha),
                        // rejeita imediatamente tokens antigos (OWASP ASVS 2.5.6)
                        if (tokenVersionClaim != null && tokenVersionClaim != currentVersion) {
                            log.warn("Sessão revogada por alteração de senha para usuário id={} (tv token={}, tv atual={})",
                                    usuarioId, tokenVersionClaim, currentVersion);
                        } else if (tokenVersionClaim == null && currentVersion > 1) {
                            log.warn("Token legado revogado após alteração de senha para usuário id={}", usuarioId);
                        } else {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            usuario, null, usuario.getAuthorities());
                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } else {
                        log.warn("Token JWT válido, mas usuário id={} não encontrado no banco (URI={})",
                                usuarioId, request.getRequestURI());
                    }
                } catch (NumberFormatException ex) {
                    log.warn("Subject inválido no token JWT para URI={}", request.getRequestURI());
                }
            } else {
                log.warn("Tentativa de acesso com token JWT inválido ou expirado (URI={}, IP={})",
                        request.getRequestURI(), request.getRemoteAddr());
            }
        }

        filterChain.doFilter(request, response);
    }
}
