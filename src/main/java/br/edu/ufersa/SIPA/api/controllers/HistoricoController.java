package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.HistoricoResponseDTO;
import br.edu.ufersa.SIPA.api.services.HistoricoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/{userId}/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping
    public ResponseEntity<HistoricoResponseDTO> obterHistorico() {
        return ResponseEntity.ok(historicoService.obterHistorico());
    }
}