package br.edu.ufersa.SIPA.freatures.plantio;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioResponseDTO;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/SIPA/plantios")
public class PlantioController {

    private final PlantioService plantioService;

    public PlantioController(PlantioService plantioService) {
        this.plantioService = plantioService;
    }

    @GetMapping
    public ResponseEntity<List<PlantioResponseDTO>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(plantioService.listar(usuario.getId(), data, status, nome));
    }

    @GetMapping("/{plantioId}")
    public ResponseEntity<PlantioResponseDTO> buscarPorId(@PathVariable Long plantioId,
                                                          @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(plantioService.buscarPorId(plantioId, usuario.getId()));
    }

    @PostMapping
    public ResponseEntity<PlantioResponseDTO> criar(@Valid @RequestBody PlantioRequestDTO dto,
                                                    @AuthenticationPrincipal Usuario usuario) {
        PlantioResponseDTO criado = plantioService.criar(usuario.getId(), dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{plantioId}")
    public ResponseEntity<PlantioResponseDTO> atualizar(@PathVariable Long plantioId,
                                                        @Valid @RequestBody PlantioRequestDTO dto,
                                                        @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(plantioService.atualizar(plantioId, usuario.getId(), dto));
    }

    @DeleteMapping("/{plantioId}")
    public ResponseEntity<Void> deletar(@PathVariable Long plantioId, @AuthenticationPrincipal Usuario usuario) {
        plantioService.deletar(plantioId, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}