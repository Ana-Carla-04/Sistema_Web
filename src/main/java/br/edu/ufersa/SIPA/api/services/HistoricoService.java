package br.edu.ufersa.SIPA.api.services;

import br.edu.ufersa.SIPA.api.dto.HistoricoResponseDTO;
import br.edu.ufersa.SIPA.domain.repositories.CustoRepository;
import br.edu.ufersa.SIPA.domain.repositories.PlantioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoricoService {

    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;

    public HistoricoService(PlantioRepository plantioRepository, CustoRepository custoRepository) {
        this.plantioRepository = plantioRepository;
        this.custoRepository = custoRepository;
    }

    @Transactional(readOnly = true)
    public HistoricoResponseDTO obterHistorico() {
        List<HistoricoResponseDTO.HistoricoPlantioDTO> plantios = plantioRepository
            .findAllByOrderByDataPlantioDesc()
            .stream()
            .map(p -> new HistoricoResponseDTO.HistoricoPlantioDTO(
                p.getId(),
                p.getNome(),
                p.getStatus(),
                p.getDataPlantio(),
                p.getArea()
            ))
            .toList();

        List<HistoricoResponseDTO.HistoricoCustoDTO> custos = custoRepository
            .findAllByOrderByDataDesc()
            .stream()
            .map(c -> new HistoricoResponseDTO.HistoricoCustoDTO(
                c.getId(),
                c.getPlantio().getId(),
                c.getPlantio().getNome(),
                c.getData(),
                c.getCategoria(),
                c.getDescricao(),
                c.getValor()
            ))
            .toList();

        return new HistoricoResponseDTO(plantios, custos);
    }
}