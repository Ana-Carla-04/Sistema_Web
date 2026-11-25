package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.UserRole;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.auth.entity.AuditLog;
import br.edu.ufersa.SIPA.freatures.auth.repository.AuditLogRepository;
import br.edu.ufersa.SIPA.freatures.auth.repository.PasswordResetTokenRepository;
import br.edu.ufersa.SIPA.freatures.auth.service.EmailNotificationService;
import br.edu.ufersa.SIPA.freatures.auth.service.PasswordRateLimiterService;
import br.edu.ufersa.SIPA.freatures.auth.service.PwnedPasswordValidatorService;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;
import br.edu.ufersa.SIPA.shared.exeception.ConflictException;
import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class CadastroService {

    private static final Logger log = LoggerFactory.getLogger(CadastroService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PwnedPasswordValidatorService passwordValidator;
    private final PasswordRateLimiterService rateLimiter;
    private final AuditLogRepository auditLogRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailNotificationService emailService;

    public CadastroService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            PwnedPasswordValidatorService passwordValidator,
            PasswordRateLimiterService rateLimiter,
            AuditLogRepository auditLogRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailNotificationService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.rateLimiter = rateLimiter;
        this.auditLogRepository = auditLogRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Usuario cadastrar(CadastroRequestDTO dto) {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(normalizedEmail)) {
            log.warn("Tentativa de cadastro com e-mail já existente.");
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(normalizedEmail);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setRole(UserRole.USER);
        usuario.setTokenVersion(1);

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Novo usuário cadastrado com sucesso: id={}", salvo.getId());
        return salvo;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));
    }

    @Transactional
    public UsuarioResponseDTO atualizarComAuditoria(
            Long usuarioId,
            CadastroUpdateRequestDTO dto,
            String ip,
            String userAgent) {

        Usuario usuario = buscarPorId(usuarioId);
        String targetEmail = usuario.getEmail();

        // Verifica limite de taxa caso o usuário esteja tentando alterar senha repetidamente
        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            rateLimiter.checkAndRecordForgotPasswordRateLimit(targetEmail, ip);
        }

        // Verificação de segurança obrigatória: confere a senha atual do usuário
        if (dto.getSenhaAtual() == null || !passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            rateLimiter.recordFailedAttempt(ip, targetEmail);
            auditLogRepository.save(new AuditLog(
                    "PROFILE_PASSWORD_VERIFICATION",
                    targetEmail,
                    ip,
                    userAgent,
                    "FAILURE",
                    "INVALID_CURRENT_PASSWORD"
            ));
            log.warn("Falha na verificação de segurança ao atualizar perfil do usuário id={}: senha atual incorreta", usuarioId);
            throw new IllegalArgumentException("Senha atual incorreta. Verifique sua senha para autorizar a alteração.");
        }

        String novoEmail = dto.getEmail().trim().toLowerCase();
        if (!usuario.getEmail().equalsIgnoreCase(novoEmail)
                && usuarioRepository.existsByEmail(novoEmail)) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(novoEmail);

        boolean mudouSenha = false;
        EmailNotificationService.RenderedEmail alertaEmail = null;

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            String nova = dto.getNovaSenha();

            if (dto.getConfirmarNovaSenha() != null && !nova.equals(dto.getConfirmarNovaSenha())) {
                rateLimiter.recordFailedAttempt(ip, novoEmail);
                auditLogRepository.save(new AuditLog(
                        "PASSWORD_CHANGE_AUTHENTICATED",
                        novoEmail,
                        ip,
                        userAgent,
                        "FAILURE",
                        "PASSWORD_CONFIRM_MISMATCH"
                ));
                throw new IllegalArgumentException("A confirmação da nova senha não coincide.");
            }

            if (passwordEncoder.matches(nova, usuario.getSenha())) {
                rateLimiter.recordFailedAttempt(ip, novoEmail);
                auditLogRepository.save(new AuditLog(
                        "PASSWORD_CHANGE_AUTHENTICATED",
                        novoEmail,
                        ip,
                        userAgent,
                        "FAILURE",
                        "SAME_AS_CURRENT_PASSWORD"
                ));
                throw new IllegalArgumentException("A nova senha deve ser diferente da senha atual.");
            }

            // Valida força da senha (≥ 12 caracteres, 3 de 4 tipos, checagem HaveIBeenPwned k-Anonymity)
            try {
                passwordValidator.validatePasswordStrength(nova, novoEmail);
            } catch (IllegalArgumentException ex) {
                rateLimiter.recordFailedAttempt(ip, novoEmail);
                auditLogRepository.save(new AuditLog(
                        "PASSWORD_CHANGE_AUTHENTICATED",
                        novoEmail,
                        ip,
                        userAgent,
                        "FAILURE",
                        "WEAK_OR_PWNED_PASSWORD"
                ));
                throw ex;
            }

            usuario.setSenha(passwordEncoder.encode(nova));
            // Invalida TODAS as sessões ativas do usuário incrementando token_version (OWASP ASVS 2.5.6)
            usuario.incrementTokenVersion();
            tokenRepository.invalidateAllActiveTokensForEmail(novoEmail, Instant.now());
            mudouSenha = true;
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        rateLimiter.resetFailedAttempts(ip, novoEmail);

        UsuarioResponseDTO response = UsuarioResponseDTO.fromEntity(atualizado);

        if (mudouSenha) {
            auditLogRepository.save(new AuditLog(
                    "PASSWORD_CHANGE_AUTHENTICATED",
                    novoEmail,
                    ip,
                    userAgent,
                    "SUCCESS",
                    null
            ));
            alertaEmail = emailService.sendPasswordChangedAlert(novoEmail, ip, userAgent);
            response.setSenhaAlterada(true);
            response.setSessoesInvalidadas(true);
            response.setAlertaSeguranca(Map.of(
                    "timestamp", alertaEmail.timestampUtc(),
                    "ipAddress", ip != null ? ip : "desconhecido",
                    "userAgent", userAgent != null ? userAgent : "Navegador Web"
            ));
            log.info("Senha alterada e sessões anteriores invalidadas para usuário id={}", usuarioId);
        } else {
            auditLogRepository.save(new AuditLog(
                    "PROFILE_UPDATE",
                    novoEmail,
                    ip,
                    userAgent,
                    "SUCCESS",
                    null
            ));
            log.info("Dados cadastrais atualizados com verificação de senha para usuário id={}", usuarioId);
        }

        return response;
    }

    @Transactional
    public void excluir(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + usuarioId);
        }
        usuarioRepository.deleteById(usuarioId);
        log.info("Conta de usuário excluída: id={}", usuarioId);
    }
}
