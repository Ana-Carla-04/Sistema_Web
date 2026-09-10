package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dto.AnaliseFinanceiraResponseDTO;
import br.edu.ufersa.SIPA.api.services.AnaliseFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/{userId}/analise-financeira")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraService analiseFinanceiraService;

    public AnaliseFinanceiraController(AnaliseFinanceiraService analiseFinanceiraService) {
        this.analiseFinanceiraService = analiseFinanceiraService;
    }

    @GetMapping
    public ResponseEntity<AnaliseFinanceiraResponseDTO> obterAnaliseFinanceira() {
        return ResponseEntity.ok(analiseFinanceiraService.obterAnaliseFinanceira());
    }
}