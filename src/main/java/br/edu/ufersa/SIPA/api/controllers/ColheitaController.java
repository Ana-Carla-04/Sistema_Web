package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.ColheitaRequest;
import br.edu.ufersa.SIPA.api.dtos.ColheitaResponse;
import br.edu.ufersa.SIPA.api.dtos.ColheitaResumoDTO;
import br.edu.ufersa.SIPA.domain.entities.Colheita;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/SIPA/{usuarioId}/colheita")
public class ColheitaController {

    private static final double KG_POR_ALQUEIRE = 115.0;

    // Calcular receita completa (POST porque envia muitos dados)
    // @PostMapping representa uma requisicao HTTP POST.
    // E usado aqui para enviar varios dados e calcular uma receita completa.
    // O caminho deste endpoint e: POST /SIPA/{usuarioId}/colheita/calcular.
    // O @RequestBody recebe esses dados no corpo da requisicao.
    @PostMapping("/calcular")
    public ResponseEntity<ColheitaResponse> calcularReceita(@RequestBody ColheitaRequest request) {

        // então não implementei aqui pra não arriscar inventar campos que conflitem
        // com o que você já tem. A lógica é a mesma dos métodos abaixo, só precisa
        // ler os valores do request e montar o response.
        return null;
    }

    // Mostrar produção líquida
    @GetMapping("/producao-liquida")
    public ResponseEntity<Double> calcularProducaoLiquida(
            @RequestParam Double producaoBruta,
            @RequestParam Double descontoUmidade) {
        double producaoLiquida = producaoBruta - (producaoBruta * descontoUmidade / 100);
        return ResponseEntity.ok(producaoLiquida);
    }

    // Mostrar equivalência em alqueire
    @GetMapping("/equivalencia-alqueire")
    public ResponseEntity<Double> calcularEquivalenciaAlqueire(
            @RequestParam Double producaoLiquida) {
        double equivalenteAlqueire = producaoLiquida / KG_POR_ALQUEIRE;
        return ResponseEntity.ok(equivalenteAlqueire);
    }

    // Mostrar receita bruta estimada
    @GetMapping("/receita-bruta")
    public ResponseEntity<Double> calcularReceitaBruta(
            @RequestParam Double equivalenteAlqueire,
            @RequestParam Double precoAlqueire) {
        double receitaBruta = equivalenteAlqueire * precoAlqueire;
        return ResponseEntity.ok(receitaBruta);
    }

    // Mostrar despesas
    @GetMapping("/despesas")
    public ResponseEntity<Double> calcularDespesas(
            @RequestParam Double receitaBruta,
            @RequestParam Double taxaMaquina) {
        double despesas = receitaBruta * taxaMaquina / 100;
        return ResponseEntity.ok(despesas);
    }

    // Mostrar saldo
    @GetMapping("/saldo")
    public ResponseEntity<Double> calcularSaldo(
            @RequestParam Double receitaBruta,
            @RequestParam Double despesas) {
        double saldo = receitaBruta - despesas;
        return ResponseEntity.ok(saldo);
    }

    // Listar todas as colheitas
    @GetMapping
    public ResponseEntity<List<Colheita>> listarTodos() {
        //  depende do ColheitaRepository (ainda vazio) - próxima etapa
        return null;
    }

    // Listar colheitas recentes (para a tabela)
    @GetMapping("/recentes")
    public ResponseEntity<List<ColheitaResumoDTO>> listarRecentes() {
        //  depende do ColheitaRepository
        return null;
    }

    // Listar colheita por lote
    @GetMapping("/lote")
    public ResponseEntity<List<Colheita>> listarLote(@RequestParam String lote) {
        //  depende do ColheitaRepository
        return null;
    }

    // Editar colheita
    // @PutMapping representa uma requisicao HTTP PUT.
    // E usado para atualizar ou substituir os dados completos de uma colheita.
    // O id identifica, por parametro na URL, qual colheita sera atualizada.
    // O @RequestBody recebe os novos dados da colheita.
    @PutMapping("/editar")
    public ResponseEntity<Colheita> editarColheita(
            @RequestParam Long id,
            @RequestBody Colheita colheita) {
        //  depende do ColheitaRepository
        return null;
    }

    
    // Deletar colheita
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletarColheita(@RequestParam Long id) {
        //  depende do ColheitaRepository
        return null;
    }

    // Adicionar colheita
    // @PostMapping tambem e usado aqui para criar uma nova colheita.
    // O caminho fica: POST /SIPA/{usuarioId}/colheita/adicionar.
    // O @RequestBody recebe os dados da colheita no corpo da requisicao.
    @PostMapping("/adicionar")
    public ResponseEntity<Colheita> adicionarColheita(@RequestBody Colheita colheita) {
        //  depende do ColheitaRepository
        return null;
    }
}