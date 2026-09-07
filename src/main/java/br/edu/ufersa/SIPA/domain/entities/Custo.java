package br.edu.ufersa.SIPA.domain.entities;

import java.time.LocalDate;

public class Custo {
    private Long id;
    private LocalDate data;
    private Plantio lote;
    private String categoria;
    private String descricao;
    private Double valor;
    private String comprovante;
}
