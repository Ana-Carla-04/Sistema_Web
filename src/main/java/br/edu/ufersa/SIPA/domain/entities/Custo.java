package br.edu.ufersa.SIPA.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "custos")
public class Custo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    // Antes era "lote" — renomeado para "plantio" para casar com a URL
    // /SIPA/plantios/{plantioId}/custos e com os DTOs.
    @ManyToOne
    @JoinColumn(name = "plantio_id", nullable = false)
    private Plantio plantio;

    @Column(nullable = false)
    private String categoria;

    private String descricao;

    @Column(nullable = false)
    private Double valor;

    private String comprovante;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Plantio getPlantio() { return plantio; }
    public void setPlantio(Plantio plantio) { this.plantio = plantio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getComprovante() { return comprovante; }
    public void setComprovante(String comprovante) { this.comprovante = comprovante; }
}