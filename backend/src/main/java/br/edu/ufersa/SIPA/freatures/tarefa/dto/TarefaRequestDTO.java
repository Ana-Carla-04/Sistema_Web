package br.edu.ufersa.SIPA.freatures.tarefa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TarefaRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;
    private LocalDate dataLimite;

    @Size(max = 120, message = "O responsável deve ter no máximo 120 caracteres")
    private String responsavel;

    @Pattern(regexp = "PRIORITARIA|AGENDADO|PLANEJADO",
            message = "A prioridade deve ser PRIORITARIA, AGENDADO ou PLANEJADO")
    private String prioridade;

    @Pattern(regexp = "PENDENTE|EM_ANDAMENTO|CONCLUIDA",
            message = "O status deve ser PENDENTE, EM_ANDAMENTO ou CONCLUIDA")
    private String status;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}