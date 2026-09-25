package br.edu.ufersa.SIPA.freatures.plantio.dto;

import br.edu.ufersa.SIPA.freatures.plantio.Plantio;

import java.time.LocalDate;


public class PlantioResponseDTO {

    private Long id;
    private String nome;
    private String variedade;
    private Double area;
    private LocalDate dataPlantio;
    private String status;

    public PlantioResponseDTO() {}

    public PlantioResponseDTO(Long id, String nome, String variedade, Double area,
                              LocalDate dataPlantio, String status) {
        this.id = id;
        this.nome = nome;
        this.variedade = variedade;
        this.area = area;
        this.dataPlantio = dataPlantio;
        this.status = status;
    }

    public static PlantioResponseDTO fromEntity(Plantio plantio) {
        return new PlantioResponseDTO(
                plantio.getId(),
                plantio.getNome(),
                plantio.getVariedade(),
                plantio.getArea(),
                plantio.getDataPlantio(),
                plantio.getStatus()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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