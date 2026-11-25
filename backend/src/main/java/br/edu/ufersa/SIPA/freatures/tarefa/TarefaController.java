package br.edu.ufersa.SIPA.freatures.tarefa;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.tarefa.dto.TarefaRequestDTO;
import br.edu.ufersa.SIPA.freatures.tarefa.dto.TarefaResponseDTO;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/SIPA")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping("/plantios/{plantioId}/tarefas")
    public ResponseEntity<List<TarefaResponseDTO>> listarPorPlantio(@PathVariable Long plantioId,
                                                                    @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(
                tarefaService.listarPorPlantio(usuario.getId(), plantioId)
                        .stream().map(TarefaResponseDTO::fromEntity).toList());
    }

    @PostMapping("/plantios/{plantioId}/tarefas")
    public ResponseEntity<TarefaResponseDTO> criar(@PathVariable Long plantioId,
                                                   @Valid @RequestBody TarefaRequestDTO dto,
                                                   @AuthenticationPrincipal Usuario usuario) {
        Tarefa criada = tarefaService.criar(usuario.getId(), plantioId, dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/SIPA/tarefas/{id}")
                .buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(TarefaResponseDTO.fromEntity(criada));
    }

    @GetMapping("/tarefas")
    public ResponseEntity<List<TarefaResponseDTO>> listarTodas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(
                tarefaService.listar(usuario.getId()).stream().map(TarefaResponseDTO::fromEntity).toList());
    }

    @GetMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(@PathVariable Long id,
                                                         @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(TarefaResponseDTO.fromEntity(tarefaService.buscarPorId(id, usuario.getId())));
    }

    @PutMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody TarefaRequestDTO dto,
                                                       @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(TarefaResponseDTO.fromEntity(tarefaService.atualizar(id, usuario.getId(), dto)));
    }

    @PatchMapping("/tarefas/{id}/concluir")
    public ResponseEntity<TarefaResponseDTO> concluir(@PathVariable Long id,
                                                      @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(TarefaResponseDTO.fromEntity(tarefaService.concluir(id, usuario.getId())));
    }

    @DeleteMapping("/tarefas/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        tarefaService.deletar(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}