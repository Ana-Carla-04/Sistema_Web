package br.edu.ufersa.SIPA.freatures.tarefa;

import br.edu.ufersa.SIPA.freatures.tarefa.dto.TarefaRequestDTO;
import br.edu.ufersa.SIPA.freatures.tarefa.dto.TarefaResponseDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
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

    // GET /SIPA/plantios/{plantioId}/tarefas -> tarefas de um plantio específico
    @GetMapping("/plantios/{plantioId}/tarefas")
    public ResponseEntity<List<TarefaResponseDTO>> listarPorPlantio(@PathVariable Long plantioId,
                                                                    HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(
                tarefaService.listarPorPlantio(usuarioId, plantioId)
                        .stream().map(TarefaResponseDTO::fromEntity).toList());
    }

    // POST /SIPA/plantios/{plantioId}/tarefas -> cria uma tarefa vinculada ao plantio
    @PostMapping("/plantios/{plantioId}/tarefas")
    public ResponseEntity<TarefaResponseDTO> criar(@PathVariable Long plantioId,
                                                   @Valid @RequestBody TarefaRequestDTO dto,
                                                   HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        Tarefa criada = tarefaService.criar(usuarioId, plantioId, dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/SIPA/tarefas/{id}")
                .buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(TarefaResponseDTO.fromEntity(criada));
    }

    // GET /SIPA/tarefas -> todas as tarefas do usuário logado
    @GetMapping("/tarefas")
    public ResponseEntity<List<TarefaResponseDTO>> listarTodas(HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(
                tarefaService.listar(usuarioId).stream().map(TarefaResponseDTO::fromEntity).toList());
    }

    // GET /SIPA/tarefas/{id}
    @GetMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(@PathVariable Long id, HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(TarefaResponseDTO.fromEntity(tarefaService.buscarPorId(id, usuarioId)));
    }

    // PUT /SIPA/tarefas/{id}
    @PutMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody TarefaRequestDTO dto,
                                                       HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(TarefaResponseDTO.fromEntity(tarefaService.atualizar(id, usuarioId, dto)));
    }

    // PATCH /SIPA/tarefas/{id}/concluir
    @PatchMapping("/tarefas/{id}/concluir")
    public ResponseEntity<TarefaResponseDTO> concluir(@PathVariable Long id, HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(TarefaResponseDTO.fromEntity(tarefaService.concluir(id, usuarioId)));
    }

    // DELETE /SIPA/tarefas/{id} -> 204 No Content
    @DeleteMapping("/tarefas/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        tarefaService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long getUsuarioLogado(HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        return (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
    }
}