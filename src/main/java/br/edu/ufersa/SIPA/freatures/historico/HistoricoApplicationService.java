package br.edu.ufersa.SIPA.freatures.historico;

import br.edu.ufersa.SIPA.freatures.historico.domain.HistoricoDomainService;
import br.edu.ufersa.SIPA.freatures.historico.dto.HistoricoResponseDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import br.edu.ufersa.SIPA.freatures.custo.Custo;
import br.edu.ufersa.SIPA.freatures.custo.CustoRepository;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistoricoApplicationService {

    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoDomainService historicoDomainService;

    public HistoricoApplicationService(
            PlantioRepository plantioRepository,
            CustoRepository custoRepository,
            UsuarioRepository usuarioRepository,
            HistoricoDomainService historicoDomainService) {
        this.plantioRepository = plantioRepository;
        this.custoRepository = custoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoDomainService = historicoDomainService;
    }

    @Transactional(readOnly = true)
    public HistoricoResponseDTO obterHistorico(UserDetails userDetails, Integer ano) {
        Long usuarioId = obterUsuarioId(userDetails);

        List<Plantio> plantios = plantioRepository.findByUsuarioId(usuarioId);
        List<Custo> custos = custoRepository.findByUsuarioIdOrderByDataDesc(usuarioId);

        if (ano != null) {
            plantios = plantios.stream()
                    .filter(p -> p.getDataPlantio() != null && p.getDataPlantio().getYear() == ano)
                    .toList();
            custos = custos.stream()
                    .filter(c -> c.getData() != null && c.getData().getYear() == ano)
                    .toList();
        }

        Map<Integer, List<Plantio>> plantiosPorAno = plantios.stream()
                .filter(p -> p.getDataPlantio() != null)
                .collect(Collectors.groupingBy(p -> p.getDataPlantio().getYear()));

        List<Integer> anosOrdenados = plantiosPorAno.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .toList();


        List<HistoricoResponseDTO.SafraConsolidadaDTO> safras = new ArrayList<>();

        for (int i = 0; i < anosOrdenados.size(); i++) {
            Integer anoAtual = anosOrdenados.get(i);
            List<Plantio> plantiosDoAno = plantiosPorAno.get(anoAtual);

            double areaTotal = plantiosDoAno.stream()
                    .mapToDouble(p -> p.getArea() != null ? p.getArea() : 0.0)   // CORREÇÃO: getArea
                    .sum();

            List<Custo> custosDoAno = custos.stream()
                    .filter(c -> c.getData() != null && c.getData().getYear() == anoAtual)
                    .toList();

            double custoTotal = custosDoAno.stream()
                    .mapToDouble(c -> c.getValor() != null ? c.getValor() : 0.0)
                    .sum();

            double receitaTotal = calcularReceitaSimulada(plantiosDoAno);

            double produtividade = historicoDomainService.calcularProdutividade(receitaTotal, areaTotal);
            double lucroEstimado = historicoDomainService.calcularLucroEstimado(receitaTotal, custoTotal);

            String tendencia = "SEM_DADOS";
            if (i + 1 < anosOrdenados.size()) {
                Integer anoAnterior = anosOrdenados.get(i + 1);
                List<Plantio> plantiosAnoAnterior = plantiosPorAno.get(anoAnterior);
                double areaAnterior = plantiosAnoAnterior.stream()
                        .mapToDouble(p -> p.getArea() != null ? p.getArea() : 0.0)   // CORREÇÃO: getArea
                        .sum();
                double receitaAnterior = calcularReceitaSimulada(plantiosAnoAnterior);
                double produtividadeAnterior =
                        historicoDomainService.calcularProdutividade(receitaAnterior, areaAnterior);
                tendencia = historicoDomainService.calcularTendencia(produtividade, produtividadeAnterior);
            }

            safras.add(new HistoricoResponseDTO.SafraConsolidadaDTO(
                    anoAtual,
                    areaTotal,
                    receitaTotal,
                    custoTotal,
                    lucroEstimado,
                    produtividade,
                    tendencia
            ));
        }

        return new HistoricoResponseDTO(safras);
    }

    private Long obterUsuarioId(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null
                || userDetails.getUsername().isBlank()) {
            throw new IllegalStateException("Usuário autenticado não identificado");
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
        return usuario.getId();
    }

    private double calcularReceitaSimulada(List<Plantio> plantios) {
        double areaTotal = plantios.stream()
                .mapToDouble(p -> p.getArea() != null ? p.getArea() : 0.0)   // CORREÇÃO: getArea
                .sum();
        return areaTotal * 5 * 700;
    }
}