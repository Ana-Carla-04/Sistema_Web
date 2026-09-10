package br.edu.ufersa.SIPA.api.dtos;

import java.time.LocalDate;
import java.util.List;

public record HistoricoResponseDTO(
    List<HistoricoPlantioDTO> plantios,
    List<HistoricoCustoDTO> custos
) {
    public record HistoricoPlantioDTO(
        Long id,
        String nome,
        String status,
        LocalDate dataPlantio,
        Double area
    ) {}

    public record HistoricoCustoDTO(
        Long id,
        Long plantioId,
        String nomePlantio,
        LocalDate data,
        String categoria,
        String descricao,
        Double valor
    ) {}
}