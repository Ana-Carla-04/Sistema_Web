package br.edu.ufersa.SIPA.freatures.tarefa.dto;

import br.edu.ufersa.SIPA.freatures.tarefa.Tarefa;

import java.time.LocalDate;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        LocalDate dataLimite,
        boolean concluida,
        Long plantioId,
        String plantioNome,
        String responsavel,
        String prioridade,
        String status
) {
    public static TarefaResponseDTO fromEntity(Tarefa t) {
        // Tarefas antigas não têm status gravado: concluída -> CONCLUIDA, senão PENDENTE.
        String status = t.isConcluida()
                ? "CONCLUIDA"
                : (t.getStatus() != null ? t.getStatus() : "PENDENTE");

        return new TarefaResponseDTO(
                t.getId(), t.getTitulo(), t.getDescricao(),
                t.getDataLimite(), t.isConcluida(),
                t.getPlantio().getId(), t.getPlantio().getNome(),
                t.getResponsavel(), t.getPrioridade(), status);
    }
}