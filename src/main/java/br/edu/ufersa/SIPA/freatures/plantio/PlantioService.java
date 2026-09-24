package br.edu.ufersa.SIPA.freatures.plantio;

import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioResponseDTO;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantioService {

    private final PlantioRepository plantioRepository;
    private final UsuarioRepository usuarioRepository;

    public PlantioService(PlantioRepository plantioRepository, UsuarioRepository usuarioRepository) {
        this.plantioRepository = plantioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /plantios?data=...&status=...&nome=...
    // Um único método para todos os filtros. Parâmetro null = sem filtro.
    public List<PlantioResponseDTO> listar(Long usuarioId, LocalDate data, String status, String nome) {
        return plantioRepository.findByUsuarioId(usuarioId).stream()
                .filter(p -> data == null || data.equals(p.getDataPlantio()))
                .filter(p -> status == null || status.equalsIgnoreCase(p.getStatus()))
                .filter(p -> nome == null || p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .map(PlantioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /plantios/{id}
    public PlantioResponseDTO buscarPorId(Long id, Long usuarioId) {
        Plantio plantio = plantioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Plantio não encontrado para este usuário: " + id));
        return PlantioResponseDTO.fromEntity(plantio);
    }

    // POST /plantios
    public PlantioResponseDTO criar(Long usuarioId, PlantioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Plantio plantio = new Plantio();
        plantio.setNome(dto.getNome());
        plantio.setVariedade(dto.getVariedade());
        plantio.setArea(dto.getArea());
        plantio.setDataPlantio(dto.getDataPlantio());
        plantio.setStatus(dto.getStatus());
        plantio.setUsuario(usuario);

        Plantio salvo = plantioRepository.save(plantio);
        return PlantioResponseDTO.fromEntity(salvo);
    }

    // PUT /plantios/{id}
    public PlantioResponseDTO atualizar(Long id, Long usuarioId, PlantioRequestDTO dto) {
        // findByIdAndUsuarioId impede que um usuário edite o plantio de outro (IDOR)
        Plantio plantio = plantioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Plantio não encontrado para este usuário: " + id));

        plantio.setNome(dto.getNome());
        plantio.setVariedade(dto.getVariedade());
        plantio.setArea(dto.getArea());
        plantio.setDataPlantio(dto.getDataPlantio());
        plantio.setStatus(dto.getStatus());

        Plantio atualizado = plantioRepository.save(plantio);
        return PlantioResponseDTO.fromEntity(atualizado);
    }

    // DELETE /plantios/{id}
    public void deletar(Long id, Long usuarioId) {
        Plantio plantio = plantioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Plantio não encontrado para este usuário: " + id));
        plantioRepository.delete(plantio);
    }
}