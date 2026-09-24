package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO;
// Importa as classes necessarias para a seguranca e autenticacao do Spring Security.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Indica que esta classe e um controlador REST e que seus metodos retornam dados da API.
@RestController
@RequestMapping("/SIPA/analise-financeira")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraService analiseFinanceiraService;

    public AnaliseFinanceiraController(AnaliseFinanceiraService analiseFinanceiraService) {
        this.analiseFinanceiraService = analiseFinanceiraService;
    }
    // Mapeia requisicoes HTTP GET para obter o historico no caminho base.
    @GetMapping
    public ResponseEntity<AnaliseFinanceiraResponseDTO> obterAnaliseFinanceira(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(analiseFinanceiraService.obterAnaliseFinanceira());
    }
}