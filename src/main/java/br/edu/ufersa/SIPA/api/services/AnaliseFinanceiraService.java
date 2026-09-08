package br.edu.ufersa.SIPA.api.services;

import br.edu.ufersa.SIPA.api.dto.AnaliseFinanceiraResponseDTO;
import br.edu.ufersa.SIPA.domain.entities.Plantio;
import br.edu.ufersa.SIPA.domain.repositories.CustoRepository;
import br.edu.ufersa.SIPA.domain.repositories.PlantioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnaliseFinanceiraService {

    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;

    public AnaliseFinanceiraService(PlantioRepository plantioRepository, CustoRepository custoRepository) {
        this.plantioRepository = plantioRepository;
        this.custoRepository = custoRepository;
    }

    @Transactional(readOnly = true)
    public AnaliseFinanceiraResponseDTO obterAnaliseFinanceira() {
        Double custoTotal = custoRepository.sumTotalCustos();
        Long totalPlantios = plantioRepository.countAll();
        Double custoMedio = totalPlantios > 0 ? custoTotal / totalPlantios : 0.0;

        // Análise por plantio
        List<AnaliseFinanceiraResponseDTO.AnalisePorPlantio> analisePorPlantio = new ArrayList<>();
        List<Plantio> plantios = plantioRepository.findAll();
        for (Plantio plantio : plantios) {
            Double custoPlantio = custoRepository.findByPlantioId(plantio.getId()).stream()
                .mapToDouble(c -> c.getValor())
                .sum();
            Long qtdCustos = custoRepository.findByPlantioId(plantio.getId()).size();
            Double custoPorArea = plantio.getArea() > 0 ? custoPlantio / plantio.getArea() : 0.0;

            analisePorPlantio.add(new AnaliseFinanceiraResponseDTO.AnalisePorPlantio(
                plantio.getId(),
                plantio.getNome(),
                custoPlantio,
                custoPorArea,
                qtdCustos
            ));
        }

        // Análise por categoria
        List<Object[]> custosPorCategoria = custoRepository.findCustosPorCategoria();
        List<AnaliseFinanceiraResponseDTO.AnalisePorCategoria> analisePorCategoria = new ArrayList<>();
        for (Object[] row : custosPorCategoria) {
            String categoria = (String) row[0];
            Double total = (Double) row[1];
            Double percentual = custoTotal > 0 ? (total / custoTotal) * 100 : 0.0;
            Long quantidade = custoRepository.findAll().stream()
                .filter(c -> c.getCategoria().equals(categoria))
                .count();

            analisePorCategoria.add(new AnaliseFinanceiraResponseDTO.AnalisePorCategoria(
                categoria,
                total,
                percentual,
                quantidade
            ));
        }

        return new AnaliseFinanceiraResponseDTO(
            custoTotal,
            custoMedio,
            analisePorPlantio,
            analisePorCategoria
        );
    }
}