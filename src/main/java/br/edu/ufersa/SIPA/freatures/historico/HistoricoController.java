package br.edu.ufersa.SIPA.freatures.historico;

import br.edu.ufersa.SIPA.freatures.historico.dto.HistoricoResponseDTO;
// Importa as classes necessarias para a seguranca e autenticacao do Spring Security.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Indica que esta classe e um controlador REST e que seus metodos retornam dados da API.
@RestController
@RequestMapping("/SIPA/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    // Mapeia requisicoes HTTP GET para obter o historico no caminho base.
    @GetMapping
    public ResponseEntity<HistoricoResponseDTO> obterHistorico(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(historicoService.obterHistorico());
    }
    
}