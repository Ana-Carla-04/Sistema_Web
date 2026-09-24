package br.edu.ufersa.SIPA.freatures.tarefa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    Optional<Tarefa> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<Tarefa> findByUsuarioId(Long usuarioId);

    // Usado pelo TarefaService.listarPorPlantio (depois de validar o plantio)
    List<Tarefa> findByPlantioId(Long plantioId);
}