package br.edu.ufersa.SIPA.freatures.plantio.dto;

import java.time.LocalDate;

// Dados recebidos do frontend ao criar ou editar um plantio.
// Não expõe a entidade JPA (Plantio) diretamente na API.
public class PlantioRequestDTO {

    private String nome;
    private String variedade;
    private Double area;
    private LocalDate dataPlantio;
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