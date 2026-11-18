package br.edu.ufersa.SIPA.freatures.plantio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PlantioRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
    private String nome;

    @Size(max = 100, message = "variedade deve ter no máximo 100 caracteres")
    private String variedade;

    @NotNull(message = "area é obrigatória")
    @Positive(message = "area deve ser maior que zero")
    private Double area;

    @NotNull(message = "dataPlantio é obrigatória")
    private LocalDate dataPlantio;

    @NotBlank(message = "status é obrigatório")
    @Size(max = 40, message = "status deve ter no máximo 40 caracteres")
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
