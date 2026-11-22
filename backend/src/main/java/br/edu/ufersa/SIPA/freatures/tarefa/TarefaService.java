package br.edu.ufersa.SIPA.freatures.tarefa;

import br.edu.ufersa.SIPA.freatures.plantio.exception.PlantioNotFoundException;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import br.edu.ufersa.SIPA.freatures.tarefa.dto.TarefaRequestDTO;
import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;
import br.edu.ufersa.SIPA.freatures.tarefa.exception.TarefaNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaService {

    private static final String STATUS_PENDENTE = "PENDENTE";
    private static final String STATUS_CONCLUIDA = "CONCLUIDA";

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlantioRepository plantioRepository;

    public TarefaService(TarefaRepository tarefaRepository,
                         UsuarioRepository usuarioRepository,
                         PlantioRepository plantioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.plantioRepository = plantioRepository;
    }

    @Transactional(readOnly = true)
    public List<Tarefa> listar(Long usuarioId) {
        return tarefaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Tarefa> listarPorPlantio(Long usuarioId, Long plantioId) {
        plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new PlantioNotFoundException(plantioId));
        return tarefaRepository.findByPlantioId(plantioId);
    }

    @Transactional(readOnly = true)
    public Tarefa buscarPorId(Long id, Long usuarioId) {
        return tarefaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new TarefaNotFoundException(id));
    }

    @Transactional
    public Tarefa criar(Long usuarioId, Long plantioId, TarefaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));

        Plantio plantio = plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new PlantioNotFoundException(plantioId));

        Tarefa t = new Tarefa();
        t.setTitulo(dto.getTitulo());
        t.setDescricao(dto.getDescricao());
        t.setDataLimite(dto.getDataLimite());
        t.setResponsavel(dto.getResponsavel());
        t.setPrioridade(dto.getPrioridade());
        aplicarStatus(t, dto.getStatus() != null ? dto.getStatus() : STATUS_PENDENTE);
        t.setUsuario(usuario);
        t.setPlantio(plantio);
        return tarefaRepository.save(t);
    }

    @Transactional
    public Tarefa atualizar(Long id, Long usuarioId, TarefaRequestDTO dto) {
        Tarefa t = buscarPorId(id, usuarioId);
        t.setTitulo(dto.getTitulo());
        t.setDescricao(dto.getDescricao());
        t.setDataLimite(dto.getDataLimite());
        t.setResponsavel(dto.getResponsavel());
        t.setPrioridade(dto.getPrioridade());
        if (dto.getStatus() != null) {
            aplicarStatus(t, dto.getStatus());
        }
        return tarefaRepository.save(t);
    }

    @Transactional
    public Tarefa concluir(Long id, Long usuarioId) {
        Tarefa t = buscarPorId(id, usuarioId);
        aplicarStatus(t, STATUS_CONCLUIDA);
        return tarefaRepository.save(t);
    }

    @Transactional
    public void deletar(Long id, Long usuarioId) {
        Tarefa t = buscarPorId(id, usuarioId);
        tarefaRepository.delete(t);
    }

    // Mantém o status e o flag "concluida" sempre coerentes entre si.
    private void aplicarStatus(Tarefa t, String status) {
        t.setStatus(status);
        t.setConcluida(STATUS_CONCLUIDA.equals(status));
    }
}