package br.edu.ufersa.SIPA.api.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponseDTO(
    Long plantiosTotal,
    Long plantiosAtivos,
    Long custosTotal,
    Double custosTotais,
    List<ResumoCustoCategoria> custosPorCategoria,
    List<PlantioResumoDTO> ultimosPlantios
) {
    public record ResumoCustoCategoria(
        String categoria,
        Double total
    ) {}

    public record PlantioResumoDTO(
        Long id,
        String nome,
        String status,
        LocalDate dataPlantio
    ) {}
}