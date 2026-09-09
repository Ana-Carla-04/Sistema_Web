package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.ColheitaRequest;
import br.edu.ufersa.SIPA.api.dtos.ColheitaResponse;
import br.edu.ufersa.SIPA.api.dtos.ColheitaResumoDTO;
import br.edu.ufersa.SIPA.domain.entities.Colheita;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/SIPA/colheita")
public class ColheitaController {

    // ========== ENDPOINTS DE CÁLCULO ==========

    // 1. Calcular receita completa (POST porque envia muitos dados)
    @PostMapping("/calcular")
    public ResponseEntity<ColheitaResponse> calcularReceita(@RequestBody ColheitaRequest request) {
        // TODO: Implementar lógica de cálculo
        // 1. Calcular produção líquida: producaoBruta - (producaoBruta * descontoUmidade / 100)
        // 2. Calcular equivalente em alqueire: producaoLiquida / 115
        // 3. Calcular receita bruta: equivalenteAlqueire * precoAlqueire
        // 4. Calcular despesas: receitaBruta * taxaMaquina / 100
        // 5. Calcular saldo: receitaBruta - despesas

        return ResponseEntity.ok(new ColheitaResponse());
    }

    // 2. Mostrar produção líquida (GET com parâmetros na URL)
    @GetMapping("/producao-liquida")
    public ResponseEntity<Double> calcularProducaoLiquida(
            @RequestParam Double producaoBruta,
            @RequestParam Double descontoUmidade) {
        // Fórmula: producaoBruta - (producaoBruta * descontoUmidade / 100)
        Double resultado = producaoBruta - (producaoBruta * descontoUmidade / 100);
        return ResponseEntity.ok(resultado);
    }

    // 3. Mostrar equivalência em alqueire
    @GetMapping("/equivalencia-alqueire")
    public ResponseEntity<Double> calcularEquivalenciaAlqueire(
            @RequestParam Double producaoLiquida) {
        // Fórmula: producaoLiquida / 115 (kg por alqueire)
        Double resultado = producaoLiquida / 115.0;
        return ResponseEntity.ok(resultado);
    }

    // 4. Mostrar receita bruta estimada
    @GetMapping("/receita-bruta")
    public ResponseEntity<Double> calcularReceitaBruta(
            @RequestParam Double equivalenteAlqueire,
            @RequestParam Double precoAlqueire) {
        // Fórmula: equivalenteAlqueire * precoAlqueire
        Double resultado = equivalenteAlqueire * precoAlqueire;
        return ResponseEntity.ok(resultado);
    }

    // 5. Mostrar despesas
    @GetMapping("/despesas")
    public ResponseEntity<Double> calcularDespesas(
            @RequestParam Double receitaBruta,
            @RequestParam Double taxaMaquina) {
        // Fórmula: receitaBruta * taxaMaquina / 100
        Double resultado = receitaBruta * taxaMaquina / 100;
        return ResponseEntity.ok(resultado);
    }

    // 6. Mostrar saldo
    @GetMapping("/saldo")
    public ResponseEntity<Double> calcularSaldo(
            @RequestParam Double receitaBruta,
            @RequestParam Double despesas) {
        // Fórmula: receitaBruta - despesas
        Double resultado = receitaBruta - despesas;
        return ResponseEntity.ok(resultado);
    }

    // ========== ENDPOINTS CRUD (IGUAL AO PLANTIO) ==========

    // 7. Listar todas as colheitas
    @GetMapping
    public ResponseEntity<List<Colheita>> listarTodos() {
        //  Buscar do banco de dados
        return ResponseEntity.ok(null);
    }

    // 8. Listar colheitas recentes (para a tabela)
    @GetMapping("/recentes")
    public ResponseEntity<List<ColheitaResumoDTO>> listarRecentes() {
        // Buscar as últimas colheitas do banco
        return ResponseEntity.ok(null);
    }

    // 9. Listar colheita por data
    @GetMapping("/data")
    public ResponseEntity<List<Colheita>> listarData(@RequestParam LocalDate data) {
        //  Buscar do banco
        return ResponseEntity.ok(null);
    }

    // 10. Listar colheita por lote
    @GetMapping("/lote")
    public ResponseEntity<List<Colheita>> listarLote(@RequestParam String lote) {
        // Buscar do banco
        return ResponseEntity.ok(null);
    }

    // 11. Editar colheita
    @PutMapping("/editar")
    public ResponseEntity<Colheita> editarColheita(
            @RequestParam Long id,
            @RequestBody Colheita colheita) {
        //  Atualizar no banco
        return ResponseEntity.ok(null);
    }

    // 12. Deletar colheita
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletarColheita(@RequestParam Long id) {
        //  Remover do banco
        return ResponseEntity.noContent().build();
    }

    // 13. Adicionar colheita
    @PostMapping("/adicionar")
    public ResponseEntity<Colheita> adicionarColheita(@RequestBody Colheita colheita) {
        //  Salvar no banco
        return ResponseEntity.ok(null);
    }
}