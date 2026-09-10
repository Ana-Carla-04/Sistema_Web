package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.AnaliseFinanceiraResponseDTO;
import br.edu.ufersa.SIPA.api.services.AnaliseFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Indica que esta classe e um controlador REST e que seus metodos retornam dados da API.
@RestController
@RequestMapping("/SIPA/{userId}/analise-financeira")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraService analiseFinanceiraService;

    public AnaliseFinanceiraController(AnaliseFinanceiraService analiseFinanceiraService) {
        this.analiseFinanceiraService = analiseFinanceiraService;
    }
    // Mapeia requisicoes HTTP GET para obter o historico no caminho base.
    @GetMapping
    public ResponseEntity<AnaliseFinanceiraResponseDTO> obterAnaliseFinanceira() {
        return ResponseEntity.ok(analiseFinanceiraService.obterAnaliseFinanceira());
    }
}