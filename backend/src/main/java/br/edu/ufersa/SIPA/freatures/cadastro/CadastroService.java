package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.UserRole;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;
import br.edu.ufersa.SIPA.shared.exeception.ConflictException;
import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;
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

        if (!usuario.getEmail().equalsIgnoreCase(dto.getEmail())
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        Usuario atualizado = usuarioRepository.save(usuario);
        log.info("Dados cadastrais atualizados para usuário id={}", usuarioId);
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
