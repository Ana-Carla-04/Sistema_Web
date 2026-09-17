package br.edu.ufersa.SIPA.freatures.colheita;

import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaRequestDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResumoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/SIPA/{usuarioId}/colheita")
public class ColheitaController {

    private final ColheitaService colheitaService;

    public ColheitaController(ColheitaService colheitaService) {
        this.colheitaService = colheitaService;
    }

    // Calcular receita completa (POST porque envia vários dados)
    // Endpoint: POST /SIPA/{usuarioId}/colheita/calcular
    @PostMapping("/calcular")
    public ResponseEntity<ColheitaResponseDTO> calcularReceita(
            @PathVariable Long usuarioId,
            @RequestBody ColheitaRequestDTO request) {
        return ResponseEntity.ok(colheitaService.calcularReceita(usuarioId, request));
    }

    // Mostrar produção líquida
    @GetMapping("/producao-liquida")
    public ResponseEntity<Double> calcularProducaoLiquida(
            @RequestParam Double producaoBruta,
            @RequestParam Double descontoUmidade) {
        return ResponseEntity.ok(colheitaService.calcularProducaoLiquida(producaoBruta, descontoUmidade));
    }

    // Mostrar equivalência em alqueire
    @GetMapping("/equivalencia-alqueire")
    public ResponseEntity<Double> calcularEquivalenciaAlqueire(@RequestParam Double producaoLiquida) {
        return ResponseEntity.ok(colheitaService.calcularEquivalenciaAlqueire(producaoLiquida));
    }

    // Mostrar receita bruta estimada
    @GetMapping("/receita-bruta")
    public ResponseEntity<Double> calcularReceitaBruta(
            @RequestParam Double equivalenteAlqueire,
            @RequestParam Double precoAlqueire) {
        return ResponseEntity.ok(colheitaService.calcularReceitaBruta(equivalenteAlqueire, precoAlqueire));
    }

    // Mostrar despesas
    @GetMapping("/despesas")
    public ResponseEntity<Double> calcularDespesas(
            @RequestParam Double receitaBruta,
            @RequestParam Double taxaMaquina) {
        return ResponseEntity.ok(colheitaService.calcularDespesas(receitaBruta, taxaMaquina));
    }

    // Mostrar saldo
    @GetMapping("/saldo")
    public ResponseEntity<Double> calcularSaldo(
            @RequestParam Double receitaBruta,
            @RequestParam Double despesas) {
        return ResponseEntity.ok(colheitaService.calcularSaldo(receitaBruta, despesas));
    }

    // Listar todas as colheitas do usuário
    @GetMapping
    public ResponseEntity<List<Colheita>> listarTodos(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(colheitaService.listarTodos(usuarioId));
    }

    // Listar colheitas recentes (para a tabela "Lançamentos de Colheitas Recentes")
    @GetMapping("/recentes")
    public ResponseEntity<List<ColheitaResumoDTO>> listarRecentes(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(colheitaService.listarRecentes(usuarioId));
    }

    // Listar colheita por lote
    @GetMapping("/lote")
    public ResponseEntity<List<Colheita>> listarLote(
            @PathVariable Long usuarioId,
            @RequestParam Long plantioId) {
        return ResponseEntity.ok(colheitaService.listarPorLote(usuarioId, plantioId));
    }

    // Editar colheita
    // Endpoint: PUT /SIPA/{usuarioId}/colheita/editar?id=...
    @PutMapping("/editar")
    public ResponseEntity<Colheita> editarColheita(
            @PathVariable Long usuarioId,
            @RequestParam Long id,
            @RequestBody Colheita colheita) {
        return ResponseEntity.ok(colheitaService.editar(usuarioId, id, colheita));
    }

    // Deletar colheita
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletarColheita(
            @PathVariable Long usuarioId,
            @RequestParam Long id) {
        colheitaService.deletar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    // Adicionar colheita
    // Endpoint: POST /SIPA/{usuarioId}/colheita/adicionar?plantioId=...
    @PostMapping("/adicionar")
    public ResponseEntity<Colheita> adicionarColheita(
            @PathVariable Long usuarioId,
            @RequestParam Long plantioId,
            @RequestBody Colheita colheita) {
        return ResponseEntity.ok(colheitaService.adicionar(usuarioId, plantioId, colheita));
    }
}