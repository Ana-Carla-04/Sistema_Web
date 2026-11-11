package br.edu.ufersa.SIPA.freatures.custo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;


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