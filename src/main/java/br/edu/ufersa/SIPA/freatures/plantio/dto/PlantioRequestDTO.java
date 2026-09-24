package br.edu.ufersa.SIPA.freatures.plantio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// Dados recebidos do frontend ao criar ou editar um plantio.
// Não expõe a entidade JPA (Plantio) diretamente na API.
public class PlantioRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    private String variedade;

    @Positive(message = "area deve ser maior que zero")
    private Double area;

    @NotNull(message = "dataPlantio é obrigatória")
    private LocalDate dataPlantio;

    @NotBlank(message = "status é obrigatório")
    private String status;

    public PlantioRequestDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getVariedade() { return variedade; }
    public void setVariedade(String variedade) { this.variedade = variedade; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public LocalDate getDataPlantio() { return dataPlantio; }
    public void setDataPlantio(LocalDate dataPlantio) { this.dataPlantio = dataPlantio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}