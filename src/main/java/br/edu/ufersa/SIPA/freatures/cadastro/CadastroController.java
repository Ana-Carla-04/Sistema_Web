package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroRequestDTO;
import br.edu.ufersa.SIPA.freatures.cadastro.dto.CadastroUpdateRequestDTO;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/SIPA/cadastro")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    // Público (ver SecurityConfig): quem ainda não tem conta não tem como estar autenticado.
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody CadastroRequestDTO dto) {
        Usuario salvo = cadastroService.cadastrar(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(location).body(UsuarioResponseDTO.fromEntity(salvo));
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

    // Anti-IDOR: só o dono da conta pode ver/editar/excluir os próprios dados.
    private void garantirQueEoProprioUsuario(Long usuarioId, Usuario usuarioLogado) {
        if (!usuarioLogado.getId().equals(usuarioId)) {

            throw new IllegalStateException("Usuário autenticado não corresponde ao recurso solicitado");
        }
    }
}