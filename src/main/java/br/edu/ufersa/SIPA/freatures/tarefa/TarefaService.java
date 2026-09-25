package br.edu.ufersa.SIPA.freatures.tarefa;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import br.edu.ufersa.SIPA.freatures.tarefa.dto.TarefaRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaService {

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

    // Leitura

    @Transactional(readOnly = true)
    public List<Tarefa> listar(Long usuarioId) {
        return tarefaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Tarefa> listarPorPlantio(Long usuarioId, Long plantioId) {

        plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Plantio não encontrado: " + plantioId));
        return tarefaRepository.findByPlantioId(plantioId);
    }

    @Transactional(readOnly = true)
    public Tarefa buscarPorId(Long id, Long usuarioId) {
        return tarefaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: " + id));
    }

    // Escrita

    @Transactional
    public Tarefa criar(Long usuarioId, Long plantioId, TarefaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        Plantio plantio = plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Plantio não encontrado: " + plantioId));

        Tarefa t = new Tarefa();
        t.setTitulo(dto.getTitulo());
        t.setDescricao(dto.getDescricao());
        t.setDataLimite(dto.getDataLimite());
        t.setConcluida(dto.isConcluida());
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
        t.setConcluida(dto.isConcluida());
        return tarefaRepository.save(t);
    }

    @Transactional
    public Tarefa concluir(Long id, Long usuarioId) {
        Tarefa t = buscarPorId(id, usuarioId);
        t.setConcluida(true);
        return tarefaRepository.save(t);
    }

    @Transactional
    public void deletar(Long id, Long usuarioId) {
        Tarefa t = buscarPorId(id, usuarioId);
        tarefaRepository.delete(t);
    }
}