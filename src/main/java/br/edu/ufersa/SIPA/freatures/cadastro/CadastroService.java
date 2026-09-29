package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.UserRole;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;
import br.edu.ufersa.SIPA.shared.exeception.ConflictException;
import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(CadastroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // nunca salvar senha em texto puro
        usuario.setRole(UserRole.USER);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));
    }

    @Transactional
    public Usuario atualizar(Long usuarioId, CadastroUpdateRequestDTO dto) {
        Usuario usuario = buscarPorId(usuarioId);

        // E-mail trocado para um já existente por outro usuário?
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + usuarioId);
        }
        usuarioRepository.deleteById(usuarioId);
    }
}