package br.edu.ufersa.SIPA.domain.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Plantio {
    private Long Id;
    private String nome;
    private String variedade;
    private Double area;
    private LocalDate dataPlantio;
    private String status;
    private List<Custo> custos = new ArrayList<>();


}
