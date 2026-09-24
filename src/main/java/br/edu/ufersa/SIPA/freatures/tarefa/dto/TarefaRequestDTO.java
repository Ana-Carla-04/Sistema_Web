package br.edu.ufersa.SIPA.freatures.tarefa.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class TarefaRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;
    private LocalDate dataLimite;
    private boolean concluida;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
}