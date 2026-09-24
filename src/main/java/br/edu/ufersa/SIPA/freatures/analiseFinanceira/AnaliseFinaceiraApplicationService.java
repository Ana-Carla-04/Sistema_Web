package br.edu.ufersa.SIPA.freatures.analiseFinanceira.Application;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinaceiraResponseDTO;
import br.edu.ufersa.SIPA.freatures.analiseFinanceira.API.mapper.AnaliseFinaceiramapper;
import br.edu.ufersa.SIPA.freatures.analiseFinanceira.AnaliseFinanceiraService;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnaliseFinaceiraApplicationService {

	// Servico responsavel pelos calculos da analise financeira.
	private final AnaliseFinanceiraService analiseFinanceiraService;
	// Repositorio usado para localizar o usuario autenticado pelo e-mail.
	private final UsuarioRepository usuarioRepository;

	// O Spring injeta as dependencias necessarias para executar o caso de uso.
	public AnaliseFinaceiraApplicationService(
			AnaliseFinanceiraService analiseFinanceiraService,
			UsuarioRepository usuarioRepository) {
		this.analiseFinanceiraService = analiseFinanceiraService;
		this.usuarioRepository = usuarioRepository;
	}

	// Executa uma consulta somente de leitura para nao alterar dados no banco.
	@Transactional(readOnly = true)
	public AnaliseFinaceiraResponseDTO obterAnaliseFinanceira(UserDetails userDetails) {
		// Impede que a analise seja executada sem uma identidade autenticada valida.
		if (userDetails == null || userDetails.getUsername() == null
				|| userDetails.getUsername().isBlank()) {
			throw new IllegalStateException("Usuário autenticado não identificado");
		}

		// O username do Spring Security e tratado como o e-mail do usuario do sistema.
		// O ID vem do banco, nunca de um parametro enviado pelo cliente.
		Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));

		// O servico calcula somente os dados associados ao ID do usuario autenticado.
		// O mapper converte o resultado para o DTO usado pela camada Application.
		return AnaliseFinaceiramapper.toApplicationResponse(
				analiseFinanceiraService.obterAnaliseFinanceira(usuario.getId()));
	}
}
