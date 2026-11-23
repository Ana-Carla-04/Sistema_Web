package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;
import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/SIPA/cadastro")
public class CadastroController {

    private static final Logger log = LoggerFactory.getLogger(CadastroController.class);

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody CadastroRequestDTO dto) {
        Usuario salvo = cadastroService.cadastrar(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(location).body(UsuarioResponseDTO.fromEntity(salvo));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuPerfil(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new UnauthorizedException("Autenticação obrigatória para acessar este recurso.");
        }
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(cadastroService.buscarPorId(usuarioLogado.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuPerfil(
            @Valid @RequestBody CadastroUpdateRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new UnauthorizedException("Autenticação obrigatória para acessar este recurso.");
        }
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(cadastroService.atualizar(usuarioLogado.getId(), dto)));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long usuarioId,
                                                          @AuthenticationPrincipal Usuario usuarioLogado) {
        garantirQueEoProprioUsuario(usuarioId, usuarioLogado);
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(cadastroService.buscarPorId(usuarioId)));
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long usuarioId,
                                                        @Valid @RequestBody CadastroUpdateRequestDTO dto,
                                                        @AuthenticationPrincipal Usuario usuarioLogado) {
        garantirQueEoProprioUsuario(usuarioId, usuarioLogado);
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(cadastroService.atualizar(usuarioId, dto)));
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> excluir(@PathVariable Long usuarioId,
                                        @AuthenticationPrincipal Usuario usuarioLogado) {
        garantirQueEoProprioUsuario(usuarioId, usuarioLogado);
        cadastroService.excluir(usuarioId);
        return ResponseEntity.noContent().build();
    }

    private void garantirQueEoProprioUsuario(Long usuarioId, Usuario usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new UnauthorizedException("Autenticação obrigatória para acessar este recurso.");
        }
        if (!usuarioLogado.getId().equals(usuarioId)) {
            log.warn("Tentativa de acesso indevido (IDOR): usuário id={} tentou acessar recurso do usuário id={}",
                    usuarioLogado.getId(), usuarioId);
            throw new AccessDeniedException("Usuário autenticado não tem permissão para acessar este recurso.");
        }
    }
}
