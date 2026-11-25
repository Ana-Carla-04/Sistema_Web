package br.edu.ufersa.SIPA.freatures.colheita.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// Dados enviados pelo simulador de receita da tela "Colheita e Receita",
// e também usados para criar/editar registros de colheita (CRUD).
// plantioId referencia o lote já cadastrado (selecionado no dropdown "Lote Selecionado").
public class ColheitaRequestDTO {

    @NotNull(message = "O plantio (lote) é obrigatório")
    private Long plantioId;

    private LocalDate data; // usado apenas no CRUD (POST/PUT); opcional no simulador

    @NotNull(message = "A produção bruta é obrigatória")
    @Positive(message = "A produção bruta deve ser maior que zero")
    private Double producaoBruta;

    private Double umidade;

    @NotNull(message = "O desconto de umidade é obrigatório")
    @DecimalMin(value = "0.0", message = "O desconto de umidade não pode ser negativo")
    @DecimalMax(value = "100.0", message = "O desconto de umidade não pode passar de 100")
    private Double descontoUmidade; // ex: 16,5% - 3,5% = 13% (arroz)

    @NotNull(message = "O preço por alqueire é obrigatório")
    @Positive(message = "O preço por alqueire deve ser maior que zero")
    private Double precoAlqueire;

    @NotNull(message = "A taxa da máquina é obrigatória")
    @DecimalMin(value = "0.0", message = "A taxa da máquina não pode ser negativa")
    @DecimalMax(value = "100.0", message = "A taxa da máquina não pode passar de 100")
    private Double taxaMaquina;

    public ColheitaRequestDTO() {}

    public Long getPlantioId() { return plantioId; }
    public void setPlantioId(Long plantioId) { this.plantioId = plantioId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Double getProducaoBruta() { return producaoBruta; }
    public void setProducaoBruta(Double producaoBruta) { this.producaoBruta = producaoBruta; }

    public Double getUmidade() { return umidade; }
    public void setUmidade(Double umidade) { this.umidade = umidade; }

    public Double getDescontoUmidade() { return descontoUmidade; }
    public void setDescontoUmidade(Double descontoUmidade) { this.descontoUmidade = descontoUmidade; }

    public Double getPrecoAlqueire() { return precoAlqueire; }
    public void setPrecoAlqueire(Double precoAlqueire) { this.precoAlqueire = precoAlqueire; }

    public Double getTaxaMaquina() { return taxaMaquina; }
    public void setTaxaMaquina(Double taxaMaquina) { this.taxaMaquina = taxaMaquina; }
}