package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnaliseFinanceiraApplicationService {

    private final AnaliseFinanceiraService analiseFinanceiraService;
    private final UsuarioRepository usuarioRepository;

    public AnaliseFinanceiraApplicationService(
            AnaliseFinanceiraService analiseFinanceiraService,
            UsuarioRepository usuarioRepository) {
        this.analiseFinanceiraService = analiseFinanceiraService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public AnaliseFinanceiraResponseDTO obterAnaliseFinanceira(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null
                || userDetails.getUsername().isBlank()) {
            throw new IllegalStateException("Usuário autenticado não identificado");
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));

        return analiseFinanceiraService.obterAnaliseFinanceira(usuario.getId());
    }
}