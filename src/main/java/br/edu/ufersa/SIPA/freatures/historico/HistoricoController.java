package br.edu.ufersa.SIPA.freatures.historico;

import br.edu.ufersa.SIPA.freatures.historico.dto.HistoricoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/historico")
public class HistoricoController {

    private final HistoricoApplicationService historicoApplicationService;

    public HistoricoController(HistoricoApplicationService historicoApplicationService) {
        this.historicoApplicationService = historicoApplicationService;
    }

    @GetMapping
    public ResponseEntity<HistoricoResponseDTO> obterHistorico(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer ano) {
        return ResponseEntity.ok(historicoApplicationService.obterHistorico(userDetails, ano));
    }
}