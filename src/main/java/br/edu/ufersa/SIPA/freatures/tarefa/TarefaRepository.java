package br.edu.ufersa.SIPA.freatures.tarefa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // garante que a tarefa pertence ao usuário logado
    Optional<Tarefa> findByIdAndUsuarioId(Long id, Long usuarioId);

    // Usado pelo TarefaController para listar as tarefas do usuário
    List<Tarefa> findByUsuarioId(Long usuarioId);
}