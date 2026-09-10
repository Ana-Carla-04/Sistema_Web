package br.edu.ufersa.SIPA.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plantios")
public class Plantio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String variedade;

    private Double area;

    @Column(nullable = false)
    private LocalDate dataPlantio;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Custo tem campo "plantio" (renomeado de "lote")
    @OneToMany(mappedBy = "plantio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Custo> custos = new ArrayList<>();

    // Colheita ainda tem campo "lote" (não renomeado)
    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Colheita> colheitas = new ArrayList<>();

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getVariedade() { return variedade; }
    public void setVariedade(String variedade) { this.variedade = variedade; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public LocalDate getDataPlantio() { return dataPlantio; }
    public void setDataPlantio(LocalDate dataPlantio) { this.dataPlantio = dataPlantio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Custo> getCustos() { return custos; }
    public void setCustos(List<Custo> custos) { this.custos = custos; }

    public List<Colheita> getColheitas() { return colheitas; }
    public void setColheitas(List<Colheita> colheitas) { this.colheitas = colheitas; }
}