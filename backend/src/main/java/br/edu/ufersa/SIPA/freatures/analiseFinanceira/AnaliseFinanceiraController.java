package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/analise-financeira")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraApplicationService analiseFinanceiraApplicationService;

    public AnaliseFinanceiraController(AnaliseFinanceiraApplicationService analiseFinanceiraApplicationService) {
        this.analiseFinanceiraApplicationService = analiseFinanceiraApplicationService;
    }

    @GetMapping
    public ResponseEntity<AnaliseFinanceiraResponseDTO> obterAnaliseFinanceira(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(analiseFinanceiraApplicationService.obterAnaliseFinanceira(userDetails));
    }
}