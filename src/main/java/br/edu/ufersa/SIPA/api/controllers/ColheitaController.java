package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.ColheitaRequest;
import br.edu.ufersa.SIPA.api.dtos.ColheitaResponse;
import br.edu.ufersa.SIPA.api.dtos.ColheitaResumoDTO;
import br.edu.ufersa.SIPA.domain.entities.Colheita;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/SIPA/colheita")
public class ColheitaController {


    //  Calcular receita completa (POST porque envia muitos dados)
    @PostMapping("/calcular")
    public ResponseEntity<ColheitaResponse> calcularReceita(@RequestBody ColheitaRequest request) {
        // Implementar lógica de cálculo
        // 1. Calcular produção líquida: producaoBruta - (producaoBruta * descontoUmidade / 100)
        // 2. Calcular equivalente em alqueire: producaoLiquida / 115
        // 3. Calcular receita bruta: equivalenteAlqueire * precoAlqueire
        // 4. Calcular despesas: receitaBruta * taxaMaquina / 100
        // 5. Calcular saldo: receitaBruta - despesas

        return null;
    }

    //  Mostrar produção líquida (GET com parâmetros na URL)
    @GetMapping("/producao-liquida")
    public ResponseEntity<Double> calcularProducaoLiquida(
            @RequestParam Double producaoBruta,
            @RequestParam Double descontoUmidade) {
        // Fórmula: producaoBruta - (producaoBruta * descontoUmidade / 100)
        return null;
    }

    //  Mostrar equivalência em alqueire
    @GetMapping("/equivalencia-alqueire")
    public ResponseEntity<Double> calcularEquivalenciaAlqueire(
            @RequestParam Double producaoLiquida) {
        // Fórmula: producaoLiquida / 115 (kg por alqueire)

        return null;
    }

    //  Mostrar receita bruta estimada
    @GetMapping("/receita-bruta")
    public ResponseEntity<Double> calcularReceitaBruta(
            @RequestParam Double equivalenteAlqueire,
            @RequestParam Double precoAlqueire) {
        // Fórmula: equivalenteAlqueire * precoAlqueire

        return null;
    }

    //  Mostrar despesas
    @GetMapping("/despesas")
    public ResponseEntity<Double> calcularDespesas(
            @RequestParam Double receitaBruta,
            @RequestParam Double taxaMaquina) {
        // Fórmula: receitaBruta * taxaMaquina / 100

        return null;
    }

    //  Mostrar saldo
    @GetMapping("/saldo")
    public ResponseEntity<Double> calcularSaldo(
            @RequestParam Double receitaBruta,
            @RequestParam Double despesas) {
        // Fórmula: receitaBruta - despesas
        return null;
    }


    //  Listar todas as colheitas
    @GetMapping
    public ResponseEntity<List<Colheita>> listarTodos() {
        //  Buscar do banco de dados
        return null;
    }

    // Listar colheitas recentes (para a tabela)
    @GetMapping("/recentes")
    public ResponseEntity<List<ColheitaResumoDTO>> listarRecentes() {
        // Buscar as últimas colheitas do banco
        return null;
    }

    //  Listar colheita por lote
    @GetMapping("/lote")
    public ResponseEntity<List<Colheita>> listarLote(@RequestParam String lote) {
        // Buscar do banco
        return ResponseEntity.ok(null);
    }

    //  Editar colheita
    @PutMapping("/editar")
    public ResponseEntity<Colheita> editarColheita(
            @RequestParam Long id,
            @RequestBody Colheita colheita) {
        //  Atualizar no banco
        return null;
    }

    //  Deletar colheita
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletarColheita(@RequestParam Long id) {
        //  Remover do banco
        return null;
    }

    //  Adicionar colheita
    @PostMapping("/adicionar")
    public ResponseEntity<Colheita> adicionarColheita(@RequestBody Colheita colheita) {
        //  Salvar no banco
        return null;
    }
}