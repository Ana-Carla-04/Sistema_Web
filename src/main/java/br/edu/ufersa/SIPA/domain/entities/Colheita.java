package br.edu.ufersa.SIPA.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "colheitas")
public class Colheita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "plantio_id", nullable = false)
    private Plantio lote;

    @Column(nullable = false)
    private Double producaoBruta;

    @Column(nullable = false)
    private Double descontoUmidade;

    private Double producaoLiquida;

    private Double equivalenteAlqueire;

    @Column(nullable = false)
    private Double precoAlqueire;

    private Double receitaBruta;

    @Column(nullable = false)
    private Double taxaMaquina;

    private Double despesas;

    private Double saldo;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Plantio getLote() { return lote; }
    public void setLote(Plantio lote) { this.lote = lote; }

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