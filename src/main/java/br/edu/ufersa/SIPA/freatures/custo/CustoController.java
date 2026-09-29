package br.edu.ufersa.SIPA.freatures.custo;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.custo.dto.CustoRequestDTO;
import br.edu.ufersa.SIPA.freatures.custo.dto.CustoResponseDTO;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/SIPA/plantios/{plantioId}/custos")
public class CustoController {

    private final CustoService custoService;

    public CustoController(CustoService custoService) {
        this.custoService = custoService;
    }

    @GetMapping
    public List<CustoResponseDTO> listar(@PathVariable Long plantioId,
                                         @AuthenticationPrincipal Usuario usuario) {
        return custoService.listar(plantioId, usuario.getId())
                .stream()
                .map(CustoResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{custoId}")
    public CustoResponseDTO buscarPorId(@PathVariable Long plantioId,
                                        @PathVariable Long custoId,
                                        @AuthenticationPrincipal Usuario usuario) {
        return CustoResponseDTO.fromEntity(
                custoService.buscarPorId(plantioId, custoId, usuario.getId()));
    }

    @PostMapping
    public ResponseEntity<CustoResponseDTO> criar(@PathVariable Long plantioId,
                                                  @Valid @RequestBody CustoRequestDTO dto,
                                                  @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustoResponseDTO.fromEntity(
                        custoService.criar(plantioId, dto, usuario.getId())));
    }

    @PutMapping("/{custoId}")
    public CustoResponseDTO atualizar(@PathVariable Long plantioId,
                                      @PathVariable Long custoId,
                                      @Valid @RequestBody CustoRequestDTO dto,
                                      @AuthenticationPrincipal Usuario usuario) {
        return CustoResponseDTO.fromEntity(
                custoService.atualizar(plantioId, custoId, dto, usuario.getId()));
    }

    @DeleteMapping("/{custoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long plantioId,
                                        @PathVariable Long custoId,
                                        @AuthenticationPrincipal Usuario usuario) {
        custoService.deletar(plantioId, custoId, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}