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

    // Injeção de Dependências: O Service precisa de 3 repositórios para funcionar.
    // Eles são declarados como "final" para garantir que não mudem depois de criados.
    private final PlantioRepository plantioRepository;
    private final CustoRepository custoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor: O Spring injeta automaticamente os repositórios quando cria esta classe.
    public DashboardApplicationService(
	    PlantioRepository plantioRepository,
	    CustoRepository custoRepository,
	    UsuarioRepository usuarioRepository) {
	this.plantioRepository = plantioRepository;
	this.custoRepository = custoRepository;
	this.usuarioRepository = usuarioRepository;
    }

    // @Transactional(readOnly = true): 
    //    - Abre uma transação com o banco.
    //    - "readOnly = true" avisa ao banco que só vamos LER dados (melhora performance e evita lock).
    // Recebe "UserDetails" (do Spring Security) para saber QUEM está logado.
    public DashboardResponseDTO obterDashboard(UserDetails userDetails) {
	
	// Extrai o ID do usuário logado a partir do UserDetails (método privado no final).
	Long usuarioId = obterUsuarioId(userDetails);
	
	// Busca no banco todos os Plantios e Custos daquele usuário específico.
	List<Plantio> plantios = plantioRepository.findByUsuarioId(usuarioId);
	List<Custo> custos = custoRepository.findByPlantioUsuarioId(usuarioId);

	// Agrupamento de Custos por Categoria:
	//    Cria um LinkedHashMap (mantém a ordem de inserção) para somar os valores.
	Map<String, Double> totaisPorCategoria = new LinkedHashMap<>();
	for (Custo custo : custos) {
	    // merge(): Se a categoria já existe no mapa, soma o valor. Se não, cria com o valor.
	    // valorSeguro() evita NullPointerException caso o valor seja nulo.
	    totaisPorCategoria.merge(
		    custo.getCategoria(),
		    valorSeguro(custo.getValor()),
		    Double::sum);
	}

	// Converte o Map para uma Lista de DTOs (ResumoCustoCategoria).
	List<DashboardResponseDTO.ResumoCustoCategoria> custosPorCategoria =
		totaisPorCategoria.entrySet().stream() // Pega cada par (chave, valor) do mapa
			.map(entry -> new DashboardResponseDTO.ResumoCustoCategoria(
				entry.getKey(), entry.getValue())) // Transforma em DTO
			.toList(); // Coleta tudo numa lista

	// Pega os "Últimos 5 Plantios":
	List<DashboardResponseDTO.PlantioResumoDTO> ultimosPlantios = plantios.stream()
		// Ordena por data de plantio (do mais recente para o mais antigo).
		// "nullsLast" garante que plantios sem data não quebrem a ordenação.
		.sorted(Comparator.comparing(
			Plantio::getDataPlantio,
			Comparator.nullsLast(Comparator.reverseOrder())))
		.limit(5) // Pega apenas os 5 primeiros
		// Converte a entidade Plantio para o DTO PlantioResumoDTO
		.map(plantio -> new DashboardResponseDTO.PlantioResumoDTO(
			plantio.getId(),
			plantio.getNome(),
			plantio.getStatus(),
			plantio.getDataPlantio()))
		.toList(); // Coleta numa lista

	// Monta e retorna o DTO final do Dashboard com TODAS as informações.
	return new DashboardResponseDTO(
		// Total de plantios do usuário (consulta direta ao banco)
		plantioRepository.countByUsuarioId(usuarioId),
		
		// Total de plantios com status "PLANTADO" (ativos)
		plantioRepository.countByUsuarioIdAndStatus(usuarioId, "PLANTADO"),
		
		// Quantidade total de custos (tamanho da lista que já temos)
		(long) custos.size(),
		
		// Soma total dos valores de todos os custos
		custos.stream().mapToDouble(custo -> valorSeguro(custo.getValor())).sum(),
		
		// A lista de custos agrupados por categoria (calculada no passo 9)
		custosPorCategoria,
		
		// A lista dos últimos 5 plantios (calculada no passo 10)
		ultimosPlantios);
    }

    // Obtém o ID do usuário logado
    private Long obterUsuarioId(UserDetails userDetails) {
	// Verifica se o UserDetails é nulo ou se o username (email) está vazio
	if (userDetails == null || userDetails.getUsername() == null
		|| userDetails.getUsername().isBlank()) {
	    throw new IllegalStateException("Usuário autenticado não identificado");
	}

	// Busca o usuário no banco pelo email (que é o username do Spring Security)
	Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
		.orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
	
	// Retorna o ID do usuário encontrado
	return usuario.getId();
    }

    // Evita NullPointerException ao somar valores
    private double valorSeguro(Double valor) {
	// Se o valor for nulo, retorna 0.0. Caso contrário, retorna o próprio valor.
	return valor == null ? 0.0 : valor;
    }
}