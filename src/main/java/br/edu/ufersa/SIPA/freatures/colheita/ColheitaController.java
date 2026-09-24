package br.edu.ufersa.SIPA.freatures.colheita;

import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaFullResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaRequestDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResumoDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/SIPA")
public class ColheitaController {

    private final ColheitaService colheitaService;

    public ColheitaController(ColheitaService colheitaService) {
        this.colheitaService = colheitaService;
    }

    // POST /SIPA/simulacoes-colheita -> simulador completo (não persiste)
    // Antes era POST /colheitas/calcular, um verbo na URL. Agora "simulação"
    // é tratada como o próprio recurso criado pela requisição: o cliente
    // "cria" uma simulação (efêmera, não persistida) e recebe o resultado.
    @PostMapping("/simulacoes-colheita")
    public ResponseEntity<ColheitaResponseDTO> criarSimulacao(@Valid @RequestBody ColheitaRequestDTO dto,
                                                              HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(colheitaService.calcularReceita(usuarioId, dto));
    }

    // GET /SIPA/colheitas/recentes -> lista resumida (não é verbo, é um filtro/coleção)
    @GetMapping("/colheitas/recentes")
    public ResponseEntity<List<ColheitaResumoDTO>> listarRecentes(HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(colheitaService.listarRecentes(usuarioId));
    }

    // GET /SIPA/colheitas -> todas as colheitas do usuário logado
    @GetMapping("/colheitas")
    public ResponseEntity<List<ColheitaFullResponseDTO>> listarTodas(HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(colheitaService.listarTodos(usuarioId));
    }

    // GET /SIPA/plantios/{plantioId}/colheitas -> colheitas de um lote específico
    @GetMapping("/plantios/{plantioId}/colheitas")
    public ResponseEntity<List<ColheitaFullResponseDTO>> listarPorLote(@PathVariable Long plantioId,
                                                                       HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(colheitaService.listarPorLote(usuarioId, plantioId));
    }

    // POST /SIPA/plantios/{plantioId}/colheitas -> registra uma colheita para o lote
    @PostMapping("/plantios/{plantioId}/colheitas")
    public ResponseEntity<ColheitaFullResponseDTO> adicionar(@PathVariable Long plantioId,
                                                             @Valid @RequestBody ColheitaRequestDTO dto,
                                                             HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        Colheita colheita = paraEntidade(dto);
        Colheita criada = colheitaService.adicionar(usuarioId, plantioId, colheita);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/SIPA/colheitas/{id}")
                .buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(ColheitaFullResponseDTO.fromEntity(criada));
    }

    // PUT /SIPA/colheitas/{id}
    @PutMapping("/colheitas/{id}")
    public ResponseEntity<ColheitaFullResponseDTO> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody ColheitaRequestDTO dto,
                                                             HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        Colheita colheita = paraEntidade(dto);
        Colheita atualizada = colheitaService.editar(usuarioId, id, colheita);
        return ResponseEntity.ok(ColheitaFullResponseDTO.fromEntity(atualizada));
    }

    // DELETE /SIPA/colheitas/{id} -> 204 No Content
    @DeleteMapping("/colheitas/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        colheitaService.deletar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    // ---- Auxiliares ----

    private Colheita paraEntidade(ColheitaRequestDTO dto) {
        Colheita colheita = new Colheita();
        colheita.setData(dto.getData());
        colheita.setProducaoBruta(dto.getProducaoBruta());
        colheita.setDescontoUmidade(dto.getDescontoUmidade());
        colheita.setPrecoAlqueire(dto.getPrecoAlqueire());
        colheita.setTaxaMaquina(dto.getTaxaMaquina());
        return colheita;
    }

    private Long getUsuarioLogado(HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        return (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
    }
}