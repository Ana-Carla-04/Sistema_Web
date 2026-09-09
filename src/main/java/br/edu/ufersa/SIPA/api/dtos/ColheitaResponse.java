package br.edu.ufersa.SIPA.api.dtos;

public class ColheitaResponse {


    private String lote;
    private Double producaoLiquida;
    private Double equivalenteAlqueire;
    private Double receitaBruta;
    private Double despesas;
    private Double saldo;

    // Construtor vazio (obrigatório)
    public ColheitaResponse() {}

    // Construtor com parâmetros
    public ColheitaResponse(String lote, Double producaoLiquida, Double equivalenteAlqueire,
                            Double receitaBruta, Double despesas, Double saldo) {
        this.lote = lote;
        this.producaoLiquida = producaoLiquida;
        this.equivalenteAlqueire = equivalenteAlqueire;
        this.receitaBruta = receitaBruta;
        this.despesas = despesas;
        this.saldo = saldo;
    }

    // Getters e Setters
//    public String getLote() { return lote; }
//    public void setLote(String lote) { this.lote = lote; }
//
//    public Double getProducaoLiquida() { return producaoLiquida; }
//    public void setProducaoLiquida(Double producaoLiquida) { this.producaoLiquida = producaoLiquida; }
//
//    public Double getEquivalenteAlqueire() { return equivalenteAlqueire; }
//    public void setEquivalenteAlqueire(Double equivalenteAlqueire) { this.equivalenteAlqueire = equivalenteAlqueire; }
//
//    public Double getReceitaBruta() { return receitaBruta; }
//    public void setReceitaBruta(Double receitaBruta) { this.receitaBruta = receitaBruta; }
//
//    public Double getDespesas() { return despesas; }
//    public void setDespesas(Double despesas) { this.despesas = despesas; }
//
//    public Double getSaldo() { return saldo; }
//    public void setSaldo(Double saldo) { this.saldo = saldo; }

}
