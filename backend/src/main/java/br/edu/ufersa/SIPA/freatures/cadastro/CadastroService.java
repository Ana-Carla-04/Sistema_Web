package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.UserRole;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;
import br.edu.ufersa.SIPA.shared.exeception.ConflictException;
import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;
import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {

    private static final Logger log = LoggerFactory.getLogger(CadastroService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(CadastroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Tentativa de cadastro com e-mail já existente.");
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setRole(UserRole.USER);

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
    public Usuario atualizar(Long usuarioId, CadastroUpdateRequestDTO dto) {
        Usuario usuario = buscarPorId(usuarioId);

        // Verificação de segurança obrigatória: confere a senha atual do usuário
        if (dto.getSenhaAtual() == null || !passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
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

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            String nova = dto.getNovaSenha();
            if (nova.length() < 8 || !nova.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
                throw new IllegalArgumentException("A nova senha deve ter no mínimo 8 caracteres, contendo letras e números.");
            }
            usuario.setSenha(passwordEncoder.encode(nova));
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        log.info("Dados cadastrais atualizados com verificação de senha para usuário id={}", usuarioId);
        return atualizado;
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
