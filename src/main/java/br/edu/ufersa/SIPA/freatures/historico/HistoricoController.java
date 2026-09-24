package br.edu.ufersa.SIPA.freatures.historico;

import br.edu.ufersa.SIPA.freatures.historico.dto.HistoricoResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping
    public ResponseEntity<HistoricoResponseDTO> obterHistorico(HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(historicoService.obterHistorico(usuarioId));
    }

    private Long getUsuarioLogado(HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        return (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
    }
}