package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.DashboardResponseDTO;
import br.edu.ufersa.SIPA.api.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Indica que esta classe e um controlador REST e que seus metodos retornam dados da API.
@RestController
@RequestMapping("/SIPA/{userId}/historico")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    // Mapeia requisicoes HTTP GET para obter o historico no caminho base.
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obterDashboard() {
        return ResponseEntity.ok(dashboardService.obterDashboard());
    }
}