package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.api.dtos.CustoRequestDTO;
import br.edu.ufersa.SIPA.api.dtos.CustoResponseDTO;
import br.edu.ufersa.SIPA.api.services.CustoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/SIPA/plantios/{plantioId}/custos")
public class CustoController {

    private final CustoService custoService;

    public CustoController(CustoService custoService) {
        this.custoService = custoService;
    }

    /** Recupera o id do usuário logado a partir da sessão HTTP. */
    private Long obterUsuarioLogado(HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        return (Long) usuarioId;
    }

    // ---------- Endpoints ----------

    @GetMapping
    public List<CustoResponseDTO> listar(@PathVariable Long plantioId,
                                         HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        return custoService.listar(plantioId, usuarioId)
                .stream()
                .map(CustoResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{custoId}")
    public CustoResponseDTO buscarPorId(@PathVariable Long plantioId,
                                        @PathVariable Long custoId,
                                        HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        return CustoResponseDTO.fromEntity(
                custoService.buscarPorId(plantioId, custoId, usuarioId));
    }

    @PostMapping
    public ResponseEntity<CustoResponseDTO> criar(@PathVariable Long plantioId,
                                                  @Valid @RequestBody CustoRequestDTO dto,
                                                  HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustoResponseDTO.fromEntity(
                        custoService.criar(plantioId, dto, usuarioId)));
    }

    @PutMapping("/{custoId}")
    public CustoResponseDTO atualizar(@PathVariable Long plantioId,
                                      @PathVariable Long custoId,
                                      @Valid @RequestBody CustoRequestDTO dto,
                                      HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        return CustoResponseDTO.fromEntity(
                custoService.atualizar(plantioId, custoId, dto, usuarioId));
    }

    @DeleteMapping("/{custoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long plantioId,
                                        @PathVariable Long custoId,
                                        HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        custoService.deletar(plantioId, custoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}