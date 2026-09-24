package br.edu.ufersa.SIPA.freatures.dashboard;

import br.edu.ufersa.SIPA.freatures.dashboard.dto.DashboardResponseDTO;
import br.edu.ufersa.SIPA.freatures.custo.CustoRepository;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
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
    public DashboardResponseDTO obterDashboard(Long usuarioId) {
        Long plantiosTotal = plantioRepository.countByUsuarioId(usuarioId);
        Long plantiosAtivos = plantioRepository.countByUsuarioIdAndStatusPlantado(usuarioId);
        Long custosTotal = custoRepository.countByUsuarioId(usuarioId);
        Double custosTotais = custoRepository.sumTotalCustosByUsuarioId(usuarioId);

        List<Object[]> custosPorCategoria = custoRepository.findCustosPorCategoriaByUsuarioId(usuarioId);
        List<DashboardResponseDTO.ResumoCustoCategoria> categorias = new ArrayList<>();
        for (Object[] row : custosPorCategoria) {
            categorias.add(new DashboardResponseDTO.ResumoCustoCategoria(
                    (String) row[0],
                    (Double) row[1]
            ));
        }

        List<DashboardResponseDTO.PlantioResumoDTO> ultimosPlantios = plantioRepository
                .findByUsuarioIdOrderByDataPlantioDesc(usuarioId)
                .stream()
                .limit(5)
                .map(p -> new DashboardResponseDTO.PlantioResumoDTO(
                        p.getId(), p.getNome(), p.getStatus(), p.getDataPlantio()))
                .toList();

        return new DashboardResponseDTO(
                plantiosTotal, plantiosAtivos, custosTotal, custosTotais,
                categorias, ultimosPlantios);
    }
}