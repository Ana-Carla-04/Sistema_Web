package br.edu.ufersa.SIPA.freatures.historico;

import br.edu.ufersa.SIPA.freatures.historico.domain.HistoricoDomainService;
import br.edu.ufersa.SIPA.freatures.historico.dto.HistoricoResponseDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import br.edu.ufersa.SIPA.freatures.custo.Custo;
import br.edu.ufersa.SIPA.freatures.custo.CustoRepository;
import br.edu.ufersa.SIPA.freatures.usuario.Usuario;
import br.edu.ufersa.SIPA.freatures.usuario.UsuarioRepository;
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

    /**
     * Obtém o histórico consolidado de safras do usuário logado.
     * 
     * @param userDetails Usuário autenticado (do Spring Security)
     * @param ano Filtro por ano (null = todos)
     * @return DTO com os dados do histórico
     */
    @Transactional(readOnly = true)
    public HistoricoResponseDTO obterHistorico(UserDetails userDetails, Integer ano) {
        // 1. Identifica o usuário logado
        Long usuarioId = obterUsuarioId(userDetails);

        // 2. Busca todos os plantios do usuário
        List<Plantio> plantios = plantioRepository.findByUsuarioId(usuarioId);

        // 3. Busca todos os custos do usuário
        List<Custo> custos = custoRepository.findByPlantioUsuarioId(usuarioId);

        // 4. Filtra por ano, se necessário
        if (ano != null) {
            plantios = plantios.stream()
                    .filter(p -> p.getDataPlantio() != null && p.getDataPlantio().getYear() == ano)
                    .toList();
            custos = custos.stream()
                    .filter(c -> c.getData() != null && c.getData().getYear() == ano)
                    .toList();
        }

        // 5. Agrupa os dados por ano para montar a tabela de safras consolidadas
        Map<Integer, List<Plantio>> plantiosPorAno = plantios.stream()
                .filter(p -> p.getDataPlantio() != null)
                .collect(Collectors.groupingBy(p -> p.getDataPlantio().getYear()));


        // Ordena os anos do mais recente para o mais antigo
        List<Integer> anosOrdenados = plantiosPorAno.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        for (int i = 0; i < anosOrdenados.size(); i++) {
            Integer anoAtual = anosOrdenados.get(i);
            List<Plantio> plantiosDoAno = plantiosPorAno.get(anoAtual);

            // Calcula totais do ano
            double areaTotal = plantiosDoAno.stream()
                    .mapToDouble(p -> p.getAreaTotal() != null ? p.getAreaTotal() : 0.0)
                    .sum();

            // Filtra os custos deste ano
            List<Custo> custosDoAno = custos.stream()
                    .filter(c -> c.getData() != null && c.getData().getYear() == anoAtual)
                    .toList();

            double custoTotal = custosDoAno.stream()
                    .mapToDouble(c -> c.getValor() != null ? c.getValor() : 0.0)
                    .sum();

            // Simulação de receita (você pode ajustar conforme a regra real)
            // Aqui estou usando um placeholder. O ideal é buscar de Colheita.
            double receitaTotal = calcularReceitaSimulada(plantiosDoAno);

            // Calcula produtividade e lucro usando o Domain Service
            double produtividade = historicoDomainService.calcularProdutividade(receitaTotal, areaTotal);
            double lucroEstimado = historicoDomainService.calcularLucroEstimado(receitaTotal, custoTotal);

            // Calcula tendência (comparando com o ano anterior)
            String tendencia = "SEM_DADOS";
            if (i + 1 < anosOrdenados.size()) {
                Integer anoAnterior = anosOrdenados.get(i + 1);
                List<Plantio> plantiosAnoAnterior = plantiosPorAno.get(anoAnterior);
                double areaAnterior = plantiosAnoAnterior.stream()
                        .mapToDouble(p -> p.getAreaTotal() != null ? p.getAreaTotal() : 0.0)
                        .sum();
                double receitaAnterior = calcularReceitaSimulada(plantiosAnoAnterior);
                double produtividadeAnterior = historicoDomainService.calcularProdutividade(receitaAnterior, areaAnterior);
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

        // 7. Monta o DTO final
        return new HistoricoResponseDTO(safras);
    }

    /**
     * Obtém o ID do usuário logado a partir do UserDetails.
     */
    private Long obterUsuarioId(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null
                || userDetails.getUsername().isBlank()) {
            throw new IllegalStateException("Usuário autenticado não identificado");
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
        return usuario.getId();
    }

    /**
     * Calcula a receita simulada de um conjunto de plantios.
     * Em um sistema real, isso viria do módulo de Colheita.
     */
    private double calcularReceitaSimulada(List<Plantio> plantios) {
        // Placeholder: assume que cada hectare gera 5 toneladas e cada tonelada vale R$ 700
        double areaTotal = plantios.stream()
                .mapToDouble(p -> p.getAreaTotal() != null ? p.getAreaTotal() : 0.0)
                .sum();
        return areaTotal * 5 * 700;
    }
}