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


    @GetMapping
    public ResponseEntity<List<PlantioResponseDTO>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome,
            HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(plantioService.listar(usuarioId, data, status, nome));
    }


    @GetMapping("/{plantioId}")
    public ResponseEntity<PlantioResponseDTO> buscarPorId(@PathVariable Long plantioId,
                                                          HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(plantioService.buscarPorId(plantioId, usuarioId));
    }


    @PostMapping
    public ResponseEntity<PlantioResponseDTO> criar(@Valid @RequestBody PlantioRequestDTO dto,
                                                    HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        PlantioResponseDTO criado = plantioService.criar(usuarioId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }


    @PutMapping("/{plantioId}")
    public ResponseEntity<PlantioResponseDTO> atualizar(@PathVariable Long plantioId,
                                                        @Valid @RequestBody PlantioRequestDTO dto,
                                                        HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        return ResponseEntity.ok(plantioService.atualizar(plantioId, usuarioId, dto));
    }

    @DeleteMapping("/{plantioId}")
    public ResponseEntity<Void> deletar(@PathVariable Long plantioId, HttpSession session) {
        Long usuarioId = getUsuarioLogado(session);
        plantioService.deletar(plantioId, usuarioId);
        return ResponseEntity.noContent().build();
    }


    private Long getUsuarioLogado(HttpSession session) {
        Object attr = session.getAttribute("idUsuario");
        if (attr == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão");
        }
        return (attr instanceof Long) ? (Long) attr : Long.valueOf(attr.toString());
    }
}