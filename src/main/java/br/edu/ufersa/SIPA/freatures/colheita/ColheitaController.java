package br.edu.ufersa.SIPA.freatures.colheita;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaFullResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaRequestDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResumoDTO;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/SIPA/colheita")
public class ColheitaController {

    private final ColheitaService colheitaService;

    public ColheitaController(ColheitaService colheitaService) {
        this.colheitaService = colheitaService;
    }

    @PostMapping("/simulacoes-colheita")
    public ResponseEntity<ColheitaResponseDTO> criarSimulacao(@Valid @RequestBody ColheitaRequestDTO dto,
                                                              @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(colheitaService.calcularReceita(usuario.getId(), dto));
    }

    @GetMapping("/colheitas/recentes")
    public ResponseEntity<List<ColheitaResumoDTO>> listarRecentes(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(colheitaService.listarRecentes(usuario.getId()));
    }

    @GetMapping("/colheitas")
    public ResponseEntity<List<ColheitaFullResponseDTO>> listarTodas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(colheitaService.listarTodos(usuario.getId()));
    }

    @GetMapping("/plantios/{plantioId}/colheitas")
    public ResponseEntity<List<ColheitaFullResponseDTO>> listarPorLote(@PathVariable Long plantioId,
                                                                       @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(colheitaService.listarPorLote(usuario.getId(), plantioId));
    }

    @PostMapping("/plantios/{plantioId}/colheitas")
    public ResponseEntity<ColheitaFullResponseDTO> adicionar(@PathVariable Long plantioId,
                                                             @Valid @RequestBody ColheitaRequestDTO dto,
                                                             @AuthenticationPrincipal Usuario usuario) {
        Colheita colheita = paraEntidade(dto);
        Colheita criada = colheitaService.adicionar(usuario.getId(), plantioId, colheita);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/SIPA/colheitas/{id}")
                .buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(ColheitaFullResponseDTO.fromEntity(criada));
    }

    @PutMapping("/colheitas/{id}")
    public ResponseEntity<ColheitaFullResponseDTO> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody ColheitaRequestDTO dto,
                                                             @AuthenticationPrincipal Usuario usuario) {
        Colheita colheita = paraEntidade(dto);
        Colheita atualizada = colheitaService.editar(usuario.getId(), id, colheita);
        return ResponseEntity.ok(ColheitaFullResponseDTO.fromEntity(atualizada));
    }

    @DeleteMapping("/colheitas/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        colheitaService.deletar(usuario.getId(), id);
        return ResponseEntity.noContent().build();
    }

    private Colheita paraEntidade(ColheitaRequestDTO dto) {
        Colheita colheita = new Colheita();
        colheita.setData(dto.getData());
        colheita.setProducaoBruta(dto.getProducaoBruta());
        colheita.setDescontoUmidade(dto.getDescontoUmidade());
        colheita.setPrecoAlqueire(dto.getPrecoAlqueire());
        colheita.setTaxaMaquina(dto.getTaxaMaquina());
        return colheita;
    }
}