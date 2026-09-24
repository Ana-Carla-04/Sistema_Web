package br.edu.ufersa.SIPA.freatures.dashboard;

import br.edu.ufersa.SIPA.freatures.dashboard.dto.DashboardResponseDTO;
import org.springframework.http.ResponseEntity;
// Importa as classes necessarias para a seguranca e autenticacao do Spring Security.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
// Indica que esta classe e um controlador REST e que seus metodos retornam dados da API.
@RestController
@RequestMapping("/SIPA/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    // Mapeia requisicoes HTTP GET para obter o historico no caminho base.
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obterDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.obterDashboard());
    }
}