package br.edu.ufersa.SIPA.freatures.dashboard;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.custo.Custo;
import br.edu.ufersa.SIPA.freatures.custo.CustoRepository;
import br.edu.ufersa.SIPA.freatures.dashboard.dto.DashboardResponseDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service // Diz ao Spring que esta classe é um "Service" (um bean gerenciado)
public class DashboardApplicationService {


    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor
    public DashboardApplicationService(
	    PlantioRepository plantioRepository,
	    CustoRepository custoRepository,
	    UsuarioRepository usuarioRepository) {
	this.plantioRepository = plantioRepository;
	this.custoRepository = custoRepository;
	this.usuarioRepository = usuarioRepository;
    }


    public DashboardResponseDTO obterDashboard(UserDetails userDetails) {
	

	Long usuarioId = obterUsuarioId(userDetails);
	

	List<Plantio> plantios = plantioRepository.findByUsuarioId(usuarioId);
	List<Custo> custos = custoRepository.findByUsuarioIdOrderByDataDesc(usuarioId);

	Map<String, Double> totaisPorCategoria = new LinkedHashMap<>();
	for (Custo custo : custos) {
	    totaisPorCategoria.merge(
		    custo.getCategoria(),
		    valorSeguro(custo.getValor()),
		    Double::sum);
	}


	List<DashboardResponseDTO.ResumoCustoCategoria> custosPorCategoria =
		totaisPorCategoria.entrySet().stream()
			.map(entry -> new DashboardResponseDTO.ResumoCustoCategoria(
				entry.getKey(), entry.getValue()))
			.toList();


	List<DashboardResponseDTO.PlantioResumoDTO> ultimosPlantios = plantios.stream()
		.sorted(Comparator.comparing(
			Plantio::getDataPlantio,
			Comparator.nullsLast(Comparator.reverseOrder())))
		.limit(5)
		.map(plantio -> new DashboardResponseDTO.PlantioResumoDTO(
			plantio.getId(),
			plantio.getNome(),
			plantio.getStatus(),
			plantio.getDataPlantio()))
		.toList();


	return new DashboardResponseDTO(
		plantioRepository.countByUsuarioId(usuarioId),

		plantioRepository.countByUsuarioIdAndStatus(usuarioId, "PLANTADO"),
		

		(long) custos.size(),
		

		custos.stream().mapToDouble(custo -> valorSeguro(custo.getValor())).sum(),
		

		custosPorCategoria,
		

		ultimosPlantios);
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


    private double valorSeguro(Double valor) {

	return valor == null ? 0.0 : valor;
    }
}