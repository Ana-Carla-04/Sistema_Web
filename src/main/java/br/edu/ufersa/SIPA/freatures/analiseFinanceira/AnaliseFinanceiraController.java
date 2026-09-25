package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.Application.AnaliseFinaceiraAplicationService;
import br.edu.ufersa.SIPA.freatures.analiseFinanceira.API.mapper.AnaliseFinaceiramapper;
import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinaceiraResponseDTO;
// Importa as classes necessarias para a seguranca e autenticacao do Spring Security.
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/SIPA/analise-financeira")
public class AnaliseFinanceiraController {

    private final AnaliseFinaceiraAplicationService analiseFinaceiraAplicationService;

    public AnaliseFinanceiraController(AnaliseFinaceiraAplicationService analiseFinaceiraAplicationService) {
        this.analiseFinaceiraAplicationService = analiseFinaceiraAplicationService;
    }
    @GetMapping
    public ResponseEntity<AnaliseFinaceiraResponseDTO> obterAnaliseFinanceira(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(AnaliseFinaceiramapper.toApiResponse(
                analiseFinaceiraAplicationService.obterAnaliseFinanceira(userDetails)));
    }
}