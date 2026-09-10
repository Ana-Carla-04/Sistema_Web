package br.edu.ufersa.SIPA.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * DTO de entrada para criar/atualizar um Custo.
 *
 * Não existe "plantioId" aqui de propósito: o plantio já vem da URL
 * (@PathVariable), então o cliente não pode "forjar" no JSON um plantioId
 * de outro produtor (over-posting).
 */
public record CustoRequestDTO(
        @NotNull(message = "A data do custo é obrigatória")
        LocalDate data,

        @NotBlank(message = "A categoria é obrigatória")
        String categoria,

        String descricao,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        Double valor,

        String comprovante
) {}