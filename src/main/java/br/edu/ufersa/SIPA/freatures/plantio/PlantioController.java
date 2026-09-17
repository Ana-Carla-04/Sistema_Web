package br.edu.ufersa.SIPA.freatures.plantio;

import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// PlantioController - Tela de Plantios
//
// GET    - Para OBTER/VER dados
// POST   - Para CRIAR/ADICIONAR dados
// PUT    - Para ATUALIZAR/SUBSTITUIR dados
// DELETE - Para REMOVER/EXCLUIR dados
@RestController
@RequestMapping("/SIPA/{usuarioId}/plantio")
public class PlantioController {

    private final PlantioService plantioService;

    public PlantioController(PlantioService plantioService) {
        this.plantioService = plantioService;
    }

    @GetMapping("/testes")
    public String testar() {
        return "Primeiro endpoint criado!!";
    }

    // lista todos os plantios do usuário
    @GetMapping
    public ResponseEntity<List<PlantioResponseDTO>> listarTodos(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(plantioService.listarTodos(usuarioId));
    }

    // listar plantio por data
    @GetMapping("/data")
    public ResponseEntity<List<PlantioResponseDTO>> listarData(
            @PathVariable Long usuarioId,
            @RequestParam LocalDate data) {
        return ResponseEntity.ok(plantioService.listarPorData(usuarioId, data));
    }

    // listar plantio por status
    @GetMapping("/status")
    public ResponseEntity<List<PlantioResponseDTO>> listarStatus(
            @PathVariable Long usuarioId,
            @RequestParam String status) {
        return ResponseEntity.ok(plantioService.listarPorStatus(usuarioId, status));
    }

    // listar plantio por nome
    @GetMapping("/nomePlantio")
    public ResponseEntity<List<PlantioResponseDTO>> listarNome(
            @PathVariable Long usuarioId,
            @RequestParam String nome) {
        return ResponseEntity.ok(plantioService.listarPorNome(usuarioId, nome));
    }

    // adicionar plantio (tela de sobreposição "+ Novo Plantio")
    // Endpoint final: POST /SIPA/{usuarioId}/plantio/adicionar
    @PostMapping("/adicionar")
    public ResponseEntity<PlantioResponseDTO> adicionarPlantio(
            @PathVariable Long usuarioId,
            @RequestBody PlantioRequestDTO plantio) {
        return ResponseEntity.ok(plantioService.adicionar(usuarioId, plantio));
    }

    // editar plantio (tela de sobreposição "Editar plantio")
    // Endpoint final: PUT /SIPA/{usuarioId}/plantio/editar/{id}
    @PutMapping("/editar/{id}")
    public ResponseEntity<PlantioResponseDTO> editarPlantio(
            @PathVariable Long usuarioId,
            @PathVariable Long id,
            @RequestBody PlantioRequestDTO plantio) {
        return ResponseEntity.ok(plantioService.editar(usuarioId, id, plantio));
    }

    // deletar plantio
    // Endpoint final: DELETE /SIPA/{usuarioId}/plantio/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletarPlantio(@PathVariable Long usuarioId, @PathVariable Long id) {
        plantioService.deletar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }
}