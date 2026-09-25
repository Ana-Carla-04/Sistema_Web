package br.edu.ufersa.SIPA.freatures.historico.dto;

import java.util.List;

public record HistoricoResponseDTO(
    List<SafraConsolidadaDTO> safras
) {
    public record SafraConsolidadaDTO(
        Integer ano,
        Double areaTotal,
        Double receitaTotal,
        Double custoTotal,
        Double lucroEstimado,
        Double produtividade,
        String tendencia
    ) {}
}