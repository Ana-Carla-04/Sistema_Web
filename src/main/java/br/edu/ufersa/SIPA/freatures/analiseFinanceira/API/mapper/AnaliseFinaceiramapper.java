package br.edu.ufersa.SIPA.freatures.analiseFinanceira.API.mapper;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.Application.dto.AnaliseFinaceiraResponseDTO;

import java.util.List;

public final class AnaliseFinaceiramapper {

    private AnaliseFinaceiramapper() {
    }

	    public static AnaliseFinaceiraResponseDTO toApplicationResponse(
		    br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO response) {
	if (response == null) {
	    return null;
	}

	List<AnaliseFinaceiraResponseDTO.AnalisePorPlantio> plantios = response.analisePorPlantio() == null
		? List.of()
		: response.analisePorPlantio().stream()
			.filter(item -> item != null)
			.map(AnaliseFinaceiramapper::toApplicationPlantio)
			.toList();

	List<AnaliseFinaceiraResponseDTO.AnalisePorCategoria> categorias = response.analisePorCategoria() == null
		? List.of()
		: response.analisePorCategoria().stream()
			.filter(item -> item != null)
			.map(AnaliseFinaceiramapper::toApplicationCategoria)
			.toList();

	return new AnaliseFinaceiraResponseDTO(
		response.custoTotal(),
		response.custoMedioPorPlantio(),
		plantios,
		categorias);
    }

	    private static AnaliseFinaceiraResponseDTO.AnalisePorPlantio toApplicationPlantio(
		    br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO.AnalisePorPlantio item) {
	return new AnaliseFinaceiraResponseDTO.AnalisePorPlantio(
		item.plantioId(),
		item.nomePlantio(),
		item.custoTotal(),
		item.custoPorArea(),
		item.quantidadeCustos());
    }

	    private static AnaliseFinaceiraResponseDTO.AnalisePorCategoria toApplicationCategoria(
		    br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO.AnalisePorCategoria item) {
	return new AnaliseFinaceiraResponseDTO.AnalisePorCategoria(
		item.categoria(),
		item.custoTotal(),
		item.percentual(),
		item.quantidade());
    }

	    public static br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO toApiResponse(
		    AnaliseFinaceiraResponseDTO response) {
		if (response == null) {
		    return null;
		}

		return new br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO(
			response.custoTotal(),
			response.custoMedioPorPlantio(),
			response.analisePorPlantio().stream()
				.map(item -> new br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO.AnalisePorPlantio(
					item.plantioId(), item.nomePlantio(), item.custoTotal(),
					item.custoPorArea(), item.quantidadeCustos()))
				.toList(),
			response.analisePorCategoria().stream()
				.map(item -> new br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO.AnalisePorCategoria(
					item.categoria(), item.custoTotal(), item.percentual(), item.quantidade()))
				.toList());
	    }
}
