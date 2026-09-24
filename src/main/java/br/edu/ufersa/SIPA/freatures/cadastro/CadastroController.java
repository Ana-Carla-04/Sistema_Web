package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.auth.UserRole;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/SIPA/usuarios")
public class CadastroController {

    private final UsuarioRepository usuarioRepository;

    public CadastroController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // POST /SIPA/usuarios -> 201 Created + Location
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody CadastroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(409).build(); // Conflict
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        // ⚠️ Troque por BCrypt quando integrar o PasswordEncoder.
        usuario.setSenha(dto.getSenha());

        usuario.setRole(UserRole.USER);

        Usuario salvo = usuarioRepository.save(usuario);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(location).body(UsuarioResponseDTO.fromEntity(salvo));
    }

    // GET /SIPA/usuarios/{usuarioId}
    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long usuarioId, HttpSession session) {
        garantirQueEoProprioUsuario(usuarioId, session);
        return usuarioRepository.findById(usuarioId)
                .map(u -> ResponseEntity.ok(UsuarioResponseDTO.fromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT /SIPA/usuarios/{usuarioId}
    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long usuarioId,
                                                        @Valid @RequestBody CadastroUpdateRequestDTO dto,
                                                        HttpSession session) {
        garantirQueEoProprioUsuario(usuarioId, session);

        return usuarioRepository.findById(usuarioId)
                .map(usuario -> {
                    // E-mail trocado para um já existente por outro usuário?
                    if (!usuario.getEmail().equals(dto.getEmail())
                            && usuarioRepository.existsByEmail(dto.getEmail())) {
                        return ResponseEntity.status(409).<UsuarioResponseDTO>build();
                    }
                    usuario.setNome(dto.getNome());
                    usuario.setEmail(dto.getEmail());
                    Usuario atualizado = usuarioRepository.save(usuario);
                    return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(atualizado));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /SIPA/usuarios/{usuarioId} -> 204 No Content
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> excluir(@PathVariable Long usuarioId, HttpSession session) {
        garantirQueEoProprioUsuario(usuarioId, session);
        if (!usuarioRepository.existsById(usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(usuarioId);
        return ResponseEntity.noContent().build();
    }

    // Prevenção de IDOR: o id da URL tem que ser o mesmo id guardado na sessão.
    private void garantirQueEoProprioUsuario(Long usuarioId, HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        Long logado = (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
        if (!logado.equals(usuarioId)) {
            throw new IllegalStateException("Usuário da sessão não corresponde ao recurso solicitado");
        }
    }
}