package br.edu.ufersa.SIPA.api.services;

import br.edu.ufersa.SIPA.api.dtos.DashboardResponseDTO;
import br.edu.ufersa.SIPA.domain.repositories.CustoRepository;
import br.edu.ufersa.SIPA.domain.repositories.PlantioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;

    public DashboardService(PlantioRepository plantioRepository, CustoRepository custoRepository) {
        this.plantioRepository = plantioRepository;
        this.custoRepository = custoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterDashboard() {
        Long plantiosTotal = plantioRepository.countAll();
        Long plantiosAtivos = plantioRepository.countByStatusPlantado();
        Long custosTotal = custoRepository.count();
        Double custosTotais = custoRepository.sumTotalCustos();

        List<Object[]> custosPorCategoria = custoRepository.findCustosPorCategoria();
        List<DashboardResponseDTO.ResumoCustoCategoria> categorias = new ArrayList<>();
        for (Object[] row : custosPorCategoria) {
            categorias.add(new DashboardResponseDTO.ResumoCustoCategoria(
                (String) row[0],
                (Double) row[1]
            ));
        }

        List<DashboardResponseDTO.PlantioResumoDTO> ultimosPlantios = plantioRepository
            .findAllByOrderByDataPlantioDesc()
            .stream()
            .limit(5)
            .map(p -> new DashboardResponseDTO.PlantioResumoDTO(
                p.getId(),
                p.getNome(),
                p.getStatus(),
                p.getDataPlantio()
            ))
            .toList();

        return new DashboardResponseDTO(
            plantiosTotal,
            plantiosAtivos,
            custosTotal,
            custosTotais,
            categorias,
            ultimosPlantios
        );
    }
}