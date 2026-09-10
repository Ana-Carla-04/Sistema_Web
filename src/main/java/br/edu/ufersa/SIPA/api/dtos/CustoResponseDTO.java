package br.edu.ufersa.SIPA.api.dtos;

import br.edu.ufersa.SIPA.domain.entities.Custo;

import java.time.LocalDate;

/**
 * DTO de saída para Custo.
 * O método estático fromEntity centraliza o mapeamento entidade → DTO.
 */
public record CustoResponseDTO(
        Long id,
        Long plantioId,
        LocalDate data,
        String categoria,
        String descricao,
        Double valor,
        String comprovante
) {
    public static CustoResponseDTO fromEntity(Custo c) {
        return new CustoResponseDTO(
                c.getId(),
                c.getPlantio() != null ? c.getPlantio().getId() : null,
                c.getData(),
                c.getCategoria(),
                c.getDescricao(),
                c.getValor(),
                c.getComprovante()
        );
    }
}