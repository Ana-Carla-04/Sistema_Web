package br.edu.ufersa.SIPA.freatures.tarefa.dto;

import br.edu.ufersa.SIPA.freatures.tarefa.Tarefa;

import java.time.LocalDate;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        LocalDate dataLimite,
        boolean concluida
) {
    public static TarefaResponseDTO fromEntity(Tarefa t) {
        return new TarefaResponseDTO(
                t.getId(), t.getTitulo(), t.getDescricao(),
                t.getDataLimite(), t.isConcluida());
    }
}