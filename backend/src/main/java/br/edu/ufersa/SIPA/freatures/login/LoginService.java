package br.edu.ufersa.SIPA.freatures.login;

import br.edu.ufersa.SIPA.freatures.auth.TokenService;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.login.dto.LoginRequestDTO;
import br.edu.ufersa.SIPA.freatures.login.dto.LoginResponseDTO;
import br.edu.ufersa.SIPA.freatures.login.exception.LoginCredenciaisInvalidasException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private static final int MAX_TENTATIVAS_FALHAS = 5;
    private static final long BLOQUEIO_SEGUNDOS = 15 * 60; // 15 minutos

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private final Map<String, TentativaLogin> tentativasPorEmail = new ConcurrentHashMap<>();

    public LoginService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        String chaveEmail = dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : "";

        verificarBloqueioTemporario(chaveEmail);

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseGet(() -> {
                    registrarFalha(chaveEmail);
                    log.warn("Tentativa de login falhou: e-mail não cadastrado ({})", chaveEmail);
                    throw new LoginCredenciaisInvalidasException();
                });

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            registrarFalha(chaveEmail);
            log.warn("Tentativa de login falhou: senha incorreta para usuário id={}", usuario.getId());
            throw new LoginCredenciaisInvalidasException();
        }

        tentativasPorEmail.remove(chaveEmail);
        String token = tokenService.generateToken(usuario);
        log.info("Login realizado com sucesso para usuário id={}", usuario.getId());
        return LoginResponseDTO.of(token, UsuarioResponseDTO.fromEntity(usuario));
    }

    private void verificarBloqueioTemporario(String chaveEmail) {
        TentativaLogin tentativa = tentativasPorEmail.get(chaveEmail);
        if (tentativa == null) return;

        if (tentativa.bloqueadoAte != null) {
            if (Instant.now().isBefore(tentativa.bloqueadoAte)) {
                log.warn("Tentativa de login bloqueada temporariamente por excesso de falhas ({})", chaveEmail);
                throw new LoginCredenciaisInvalidasException(
                        "Muitas tentativas de login malsucedidas. Aguarde alguns minutos e tente novamente.");
            } else {
                tentativasPorEmail.remove(chaveEmail);
            }
        }
    }

    private void registrarFalha(String chaveEmail) {
        if (chaveEmail.isEmpty()) return;
        tentativasPorEmail.compute(chaveEmail, (k, atual) -> {
            int novasFalhas = (atual == null ? 0 : atual.falhas) + 1;
            Instant bloqueio = novasFalhas >= MAX_TENTATIVAS_FALHAS
                    ? Instant.now().plusSeconds(BLOQUEIO_SEGUNDOS)
                    : null;
            return new TentativaLogin(novasFalhas, bloqueio);
        });
    }

    private record TentativaLogin(int falhas, Instant bloqueadoAte) {}
}
