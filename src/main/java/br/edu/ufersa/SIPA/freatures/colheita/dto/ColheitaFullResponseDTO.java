package br.edu.ufersa.SIPA.freatures.colheita.dto;

import br.edu.ufersa.SIPA.freatures.colheita.Colheita;

import java.time.LocalDate;

// Representa uma colheita PERSISTIDA (com id e campos calculados).
// Não expõe a entidade Colheita (nem o Plantio associado) — evita recursão
// infinita no JSON e vazamento do modelo interno.
public class ColheitaFullResponseDTO {

    private Long id;
    private LocalDate data;
    private Long plantioId;
    private String plantioNome;
    private Double producaoBruta;
    private Double descontoUmidade;
    private Double producaoLiquida;
    private Double equivalenteAlqueire;
    private Double precoAlqueire;
    private Double receitaBruta;
    private Double taxaMaquina;
    private Double despesas;
    private Double saldo;

    public ColheitaFullResponseDTO() {}

    public static ColheitaFullResponseDTO fromEntity(Colheita c) {
        ColheitaFullResponseDTO dto = new ColheitaFullResponseDTO();
        dto.id = c.getId();
        dto.data = c.getData();
        if (c.getLote() != null) {
            dto.plantioId = c.getLote().getId();
            dto.plantioNome = c.getLote().getNome();
        }
        dto.producaoBruta = c.getProducaoBruta();
        dto.descontoUmidade = c.getDescontoUmidade();
        dto.producaoLiquida = c.getProducaoLiquida();
        dto.equivalenteAlqueire = c.getEquivalenteAlqueire();
        dto.precoAlqueire = c.getPrecoAlqueire();
        dto.receitaBruta = c.getReceitaBruta();
        dto.taxaMaquina = c.getTaxaMaquina();
        dto.despesas = c.getDespesas();
        dto.saldo = c.getSaldo();
        return dto;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Long getPlantioId() { return plantioId; }
    public void setPlantioId(Long plantioId) { this.plantioId = plantioId; }

    public String getPlantioNome() { return plantioNome; }
    public void setPlantioNome(String plantioNome) { this.plantioNome = plantioNome; }

    public Double getProducaoBruta() { return producaoBruta; }
    public void setProducaoBruta(Double producaoBruta) { this.producaoBruta = producaoBruta; }

    public Double getDescontoUmidade() { return descontoUmidade; }
    public void setDescontoUmidade(Double descontoUmidade) { this.descontoUmidade = descontoUmidade; }

    public Double getProducaoLiquida() { return producaoLiquida; }
    public void setProducaoLiquida(Double producaoLiquida) { this.producaoLiquida = producaoLiquida; }

    public Double getEquivalenteAlqueire() { return equivalenteAlqueire; }
    public void setEquivalenteAlqueire(Double equivalenteAlqueire) { this.equivalenteAlqueire = equivalenteAlqueire; }

    public Double getPrecoAlqueire() { return precoAlqueire; }
    public void setPrecoAlqueire(Double precoAlqueire) { this.precoAlqueire = precoAlqueire; }

    public Double getReceitaBruta() { return receitaBruta; }
    public void setReceitaBruta(Double receitaBruta) { this.receitaBruta = receitaBruta; }

    public Double getTaxaMaquina() { return taxaMaquina; }
    public void setTaxaMaquina(Double taxaMaquina) { this.taxaMaquina = taxaMaquina; }

    public Double getDespesas() { return despesas; }
    public void setDespesas(Double despesas) { this.despesas = despesas; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }
}