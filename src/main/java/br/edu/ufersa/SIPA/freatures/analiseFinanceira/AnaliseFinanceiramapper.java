package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO;

import java.util.List;

public final class AnaliseFinanceiramapper {

    private AnaliseFinanceiramapper() {
    }

	    public static AnaliseFinanceiraResponseDTO toApplicationResponse(
		    br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO response) {
	if (response == null) {
	    return null;
	}

	List<AnaliseFinanceiraResponseDTO.AnalisePorPlantio> plantios = response.analisePorPlantio() == null
		? List.of()
		: response.analisePorPlantio().stream()
			.filter(item -> item != null)
			.map(AnaliseFinanceiramapper::toApplicationPlantio)
			.toList();

	List<AnaliseFinanceiraResponseDTO.AnalisePorCategoria> categorias = response.analisePorCategoria() == null
		? List.of()
		: response.analisePorCategoria().stream()
			.filter(item -> item != null)
			.map(AnaliseFinanceiramapper::toApplicationCategoria)
			.toList();

	return new AnaliseFinanceiraResponseDTO(
		response.custoTotal(),
		response.custoMedioPorPlantio(),
		plantios,
		categorias);
    }

	    private static AnaliseFinanceiraResponseDTO.AnalisePorPlantio toApplicationPlantio(
		    br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO.AnalisePorPlantio item) {
	return new AnaliseFinanceiraResponseDTO.AnalisePorPlantio(
		item.plantioId(),
		item.nomePlantio(),
		item.custoTotal(),
		item.custoPorArea(),
		item.quantidadeCustos());
    }

	    private static AnaliseFinanceiraResponseDTO.AnalisePorCategoria toApplicationCategoria(
		    br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO.AnalisePorCategoria item) {
	return new AnaliseFinanceiraResponseDTO.AnalisePorCategoria(
		item.categoria(),
		item.custoTotal(),
		item.percentual(),
		item.quantidade());
    }

	    public static br.edu.ufersa.SIPA.freatures.analiseFinanceira.dto.AnaliseFinanceiraResponseDTO toApiResponse(
		    AnaliseFinanceiraResponseDTO response) {
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
