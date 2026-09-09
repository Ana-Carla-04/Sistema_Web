package br.edu.ufersa.SIPA.api.dtos;

public class ColheitaResumoDTO {
    private String lote;
    private Double producaoBruta;
    private Double precoVenda;
    private Double receitaLiquida;

    // Getters e Setters
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public Double getProducaoBruta() { return producaoBruta; }
    public void setProducaoBruta(Double producaoBruta) { this.producaoBruta = producaoBruta; }

    public Double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(Double precoVenda) { this.precoVenda = precoVenda; }

    public Double getReceitaLiquida() { return receitaLiquida; }
    public void setReceitaLiquida(Double receitaLiquida) { this.receitaLiquida = receitaLiquida; }
}

