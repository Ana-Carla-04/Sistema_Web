package br.edu.ufersa.SIPA.freatures.dashboard;

import br.edu.ufersa.SIPA.freatures.dashboard.dto.DashboardResponseDTO;
import br.edu.ufersa.SIPA.freatures.auth.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/dashboard")
public class DashboardController {

    private final DashboardApplicationService dashboardApplicationService;

    public DashboardController(DashboardApplicationService dashboardApplicationService) {
        this.dashboardApplicationService = dashboardApplicationService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obterDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardApplicationService.obterDashboard(userDetails));
    }
}