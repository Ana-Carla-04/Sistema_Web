package br.edu.ufersa.SIPA.freatures.colheita.dto;

import java.time.LocalDate;

// Dados enviados pelo simulador de receita da tela "Colheita e Receita",
// e também usados para criar/editar registros de colheita (CRUD).
// plantioId referencia o lote já cadastrado (selecionado no dropdown "Lote Selecionado").
public class ColheitaRequestDTO {

    private Long plantioId;
    private LocalDate data; // usado apenas no CRUD (POST/PUT); opcional no simulador
    private Double producaoBruta;
    private Double umidade;
    private Double descontoUmidade; // ex: 16,5% - 3,5% = 13% (arroz)
    private Double precoAlqueire;
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