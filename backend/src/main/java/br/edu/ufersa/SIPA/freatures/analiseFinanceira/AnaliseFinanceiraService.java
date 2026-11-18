package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.custo.CustoRepository;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnaliseFinanceiraService {

    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;

    public AnaliseFinanceiraService(PlantioRepository plantioRepository, CustoRepository custoRepository) {
        this.plantioRepository = plantioRepository;
        this.custoRepository = custoRepository;
    }

    @Transactional(readOnly = true)
    public AnaliseFinanceiraResponseDTO obterAnaliseFinanceira(Long usuarioId) {
        List<Plantio> plantios = plantioRepository.findByUsuarioId(usuarioId);
        Long totalPlantios = (long) plantios.size();

        List<AnaliseFinanceiraResponseDTO.AnalisePorPlantio> analisePorPlantio = new ArrayList<>();
        Map<String, Double> totaisPorCategoria = new LinkedHashMap<>();
        Map<String, Long> quantidadesPorCategoria = new LinkedHashMap<>();
        Double custoTotal = 0.0;

        for (Plantio plantio : plantios) {
            Double custoPlantio = plantio.getCustos().stream()
                    .mapToDouble(c -> c.getValor() == null ? 0.0 : c.getValor())
                    .sum();
            Long qtdCustos = (long) plantio.getCustos().size();
            Double area = plantio.getArea();
            Double custoPorArea = area != null && area > 0 ? custoPlantio / area : 0.0;
            custoTotal += custoPlantio;

            plantio.getCustos().forEach(custo -> {
                String categoria = custo.getCategoria();
                Double valor = custo.getValor() == null ? 0.0 : custo.getValor();
                totaisPorCategoria.merge(categoria, valor, Double::sum);
                quantidadesPorCategoria.merge(categoria, 1L, Long::sum);
            });

            analisePorPlantio.add(new AnaliseFinanceiraResponseDTO.AnalisePorPlantio(
                    plantio.getId(),
                    plantio.getNome(),
                    custoPlantio,
                    custoPorArea,
                    qtdCustos
            ));
        }

        List<AnaliseFinanceiraResponseDTO.AnalisePorCategoria> analisePorCategoria = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totaisPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            Double total = entry.getValue();
            Double percentual = custoTotal > 0 ? (total / custoTotal) * 100 : 0.0;
            Long quantidade = quantidadesPorCategoria.getOrDefault(categoria, 0L);

            analisePorCategoria.add(new AnaliseFinanceiraResponseDTO.AnalisePorCategoria(
                    categoria,
                    total,
                    percentual,
                    quantidade
            ));
        }

        return new AnaliseFinanceiraResponseDTO(
                custoTotal,
                totalPlantios > 0 ? custoTotal / totalPlantios : 0.0,
                analisePorPlantio,
                analisePorCategoria
        );
    }
}