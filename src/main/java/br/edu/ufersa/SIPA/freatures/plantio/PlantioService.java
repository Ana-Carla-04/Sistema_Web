package br.edu.ufersa.SIPA.freatures.plantio;

import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.dto.PlantioResponseDTO;
import br.edu.ufersa.SIPA.freatures.usuario.Usuario;
import br.edu.ufersa.SIPA.freatures.usuario.UsuarioRepository;
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

    public List<PlantioResponseDTO> listarTodos(Long usuarioId) {
        return plantioRepository.findByUsuarioId(usuarioId).stream()
                .map(PlantioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PlantioResponseDTO> listarPorData(Long usuarioId, LocalDate data) {
        return plantioRepository.findByUsuarioIdAndDataPlantio(usuarioId, data).stream()
                .map(PlantioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PlantioResponseDTO> listarPorStatus(Long usuarioId, String status) {
        return plantioRepository.findByUsuarioIdAndStatus(usuarioId, status).stream()
                .map(PlantioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PlantioResponseDTO> listarPorNome(Long usuarioId, String nome) {
        return plantioRepository.findByUsuarioIdAndNomeContainingIgnoreCase(usuarioId, nome).stream()
                .map(PlantioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PlantioResponseDTO adicionar(Long usuarioId, PlantioRequestDTO dto) {
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

    public PlantioResponseDTO editar(Long usuarioId, Long id, PlantioRequestDTO dto) {
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

    public void deletar(Long usuarioId, Long id) {
        Plantio plantio = plantioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Plantio não encontrado para este usuário: " + id));
        plantioRepository.delete(plantio);
    }
}