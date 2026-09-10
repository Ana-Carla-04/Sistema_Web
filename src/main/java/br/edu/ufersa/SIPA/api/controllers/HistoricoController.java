package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.HistoricoResponseDTO;
import br.edu.ufersa.SIPA.api.services.HistoricoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Indica que esta classe e um controlador REST e que seus metodos retornam dados da API.
@RestController
@RequestMapping("/SIPA/{userId}/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    // Mapeia requisicoes HTTP GET para obter o historico no caminho base.
    @GetMapping
    public ResponseEntity<HistoricoResponseDTO> obterHistorico() {
        return ResponseEntity.ok(historicoService.obterHistorico());
    }
    
}