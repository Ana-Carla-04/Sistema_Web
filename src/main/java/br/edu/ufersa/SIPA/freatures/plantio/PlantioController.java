package br.edu.ufersa.SIPA.freatures.plantio;

import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioResponseDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    // GET /SIPA/plantios
    // GET /SIPA/plantios?data=2026-09-01&status=ATIVO&nome=milho
    @GetMapping
    public ResponseEntity<List<PlantioResponseDTO>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome,
            HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(plantioService.listar(usuarioId, data, status, nome));
    }

    // GET /SIPA/plantios/{plantioId}
    @GetMapping("/{plantioId}")
    public ResponseEntity<PlantioResponseDTO> buscarPorId(@PathVariable Long plantioId,
                                                          HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(plantioService.buscarPorId(plantioId, usuarioId));
    }

    // POST /SIPA/plantios -> 201 Created + Location
    @PostMapping
    public ResponseEntity<PlantioResponseDTO> criar(@Valid @RequestBody PlantioRequestDTO dto,
                                                    HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        PlantioResponseDTO criado = plantioService.criar(usuarioId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    // PUT /SIPA/plantios/{plantioId}
    @PutMapping("/{plantioId}")
    public ResponseEntity<PlantioResponseDTO> atualizar(@PathVariable Long plantioId,
                                                        @Valid @RequestBody PlantioRequestDTO dto,
                                                        HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(plantioService.atualizar(plantioId, usuarioId, dto));
    }

    // DELETE /SIPA/plantios/{plantioId} -> 204 No Content
    @DeleteMapping("/{plantioId}")
    public ResponseEntity<Void> deletar(@PathVariable Long plantioId, HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        plantioService.deletar(plantioId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ---- Método auxiliar privado (substitui o SessaoUtil) ----
    // IMPORTANTE: "idUsuario" precisa bater EXATAMENTE com o nome usado no
    // seu LoginController em session.setAttribute(...).
    private Long getUsuarioLogado(HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        return (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
    }
}