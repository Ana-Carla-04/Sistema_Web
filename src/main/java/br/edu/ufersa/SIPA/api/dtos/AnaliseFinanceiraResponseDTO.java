package br.edu.ufersa.SIPA.api.dto;

import java.time.LocalDate;
import java.util.List;

public record AnaliseFinanceiraResponseDTO(
    Double custoTotal,
    Double custoMedioPorPlantio,
    List<AnalisePorPlantio> analisePorPlantio,
    List<AnalisePorCategoria> analisePorCategoria
) {
    public record AnalisePorPlantio(
        Long plantioId,
        String nomePlantio,
        Double custoTotal,
        Double custoPorArea,
        Long quantidadeCustos
    ) {}

    public record AnalisePorCategoria(
        String categoria,
        Double custoTotal,
        Double percentual,
        Long quantidade
    ) {}
}