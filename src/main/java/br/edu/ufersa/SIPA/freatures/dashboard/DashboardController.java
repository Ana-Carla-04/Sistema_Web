package br.edu.ufersa.SIPA.freatures.dashboard;

import br.edu.ufersa.SIPA.freatures.dashboard.dto.DashboardResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obterDashboard(HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(dashboardService.obterDashboard(usuarioId));
    }

    private Long getUsuarioLogado(HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        return (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
    }
}