package br.edu.ufersa.SIPA.freatures.colheita;

import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaFullResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaRequestDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResponseDTO;
import br.edu.ufersa.SIPA.freatures.colheita.dto.ColheitaResumoDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColheitaService {

    private static final double KG_POR_ALQUEIRE = 115.0;

    private final ColheitaRepository colheitaRepository;
    private final PlantioRepository plantioRepository;

    public ColheitaService(ColheitaRepository colheitaRepository, PlantioRepository plantioRepository) {
        this.colheitaRepository = colheitaRepository;
        this.plantioRepository = plantioRepository;
    }

    // ---------- Cálculos individuais ----------

    public double calcularProducaoLiquida(double producaoBruta, double descontoUmidade) {
        return producaoBruta - (producaoBruta * descontoUmidade / 100);
    }

    public double calcularEquivalenciaAlqueire(double producaoLiquida) {
        return producaoLiquida / KG_POR_ALQUEIRE;
    }

    public double calcularReceitaBruta(double equivalenteAlqueire, double precoAlqueire) {
        return equivalenteAlqueire * precoAlqueire;
    }

    public double calcularDespesas(double receitaBruta, double taxaMaquina) {
        return receitaBruta * taxaMaquina / 100;
    }

    public double calcularSaldo(double receitaBruta, double despesas) {
        return receitaBruta - despesas;
    }

    // ---------- Simulador (POST /colheitas/calcular) ----------

    public ColheitaResponseDTO calcularReceita(Long usuarioId, ColheitaRequestDTO request) {
        Plantio plantio = plantioRepository.findByIdAndUsuarioId(request.getPlantioId(), usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plantio não encontrado para este usuário: " + request.getPlantioId()));

        double producaoLiquida = calcularProducaoLiquida(request.getProducaoBruta(), request.getDescontoUmidade());
        double equivalenteAlqueire = calcularEquivalenciaAlqueire(producaoLiquida);
        double receitaBruta = calcularReceitaBruta(equivalenteAlqueire, request.getPrecoAlqueire());
        double despesas = calcularDespesas(receitaBruta, request.getTaxaMaquina());
        double saldo = calcularSaldo(receitaBruta, despesas);

        return new ColheitaResponseDTO(plantio.getNome(), producaoLiquida, equivalenteAlqueire,
                receitaBruta, despesas, saldo);
    }

    // ---------- CRUD (agora devolve DTO) ----------

    public List<ColheitaFullResponseDTO> listarTodos(Long usuarioId) {
        return colheitaRepository.findByUsuarioId(usuarioId).stream()
                .map(ColheitaFullResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ColheitaResumoDTO> listarRecentes(Long usuarioId) {
        return colheitaRepository.findRecentesByUsuarioId(usuarioId).stream()
                .map(this::toResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ColheitaFullResponseDTO> listarPorLote(Long usuarioId, Long plantioId) {
        plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plantio não encontrado para este usuário: " + plantioId));
        return colheitaRepository.findByLoteId(plantioId).stream()
                .map(ColheitaFullResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Colheita adicionar(Long usuarioId, Long plantioId, Colheita colheita) {
        Plantio plantio = plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plantio não encontrado para este usuário: " + plantioId));

        colheita.setLote(plantio);
        if (colheita.getData() == null) {
            colheita.setData(LocalDate.now());
        }
        preencherCamposCalculados(colheita);
        return colheitaRepository.save(colheita);
    }

    public Colheita editar(Long usuarioId, Long id, Colheita novosDados) {
        Colheita colheita = colheitaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Colheita não encontrada para este usuário: " + id));

        colheita.setData(novosDados.getData());
        colheita.setProducaoBruta(novosDados.getProducaoBruta());
        colheita.setDescontoUmidade(novosDados.getDescontoUmidade());
        colheita.setPrecoAlqueire(novosDados.getPrecoAlqueire());
        colheita.setTaxaMaquina(novosDados.getTaxaMaquina());
        preencherCamposCalculados(colheita);

        return colheitaRepository.save(colheita);
    }

    public void deletar(Long usuarioId, Long id) {
        Colheita colheita = colheitaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Colheita não encontrada para este usuário: " + id));
        colheitaRepository.delete(colheita);
    }

    // ---------- Auxiliares ----------

    private void preencherCamposCalculados(Colheita colheita) {
        double producaoLiquida = calcularProducaoLiquida(colheita.getProducaoBruta(), colheita.getDescontoUmidade());
        double equivalenteAlqueire = calcularEquivalenciaAlqueire(producaoLiquida);
        double receitaBruta = calcularReceitaBruta(equivalenteAlqueire, colheita.getPrecoAlqueire());
        double despesas = calcularDespesas(receitaBruta, colheita.getTaxaMaquina());
        double saldo = calcularSaldo(receitaBruta, despesas);

        colheita.setProducaoLiquida(producaoLiquida);
        colheita.setEquivalenteAlqueire(equivalenteAlqueire);
        colheita.setReceitaBruta(receitaBruta);
        colheita.setDespesas(despesas);
        colheita.setSaldo(saldo);
    }

    private ColheitaResumoDTO toResumoDTO(Colheita colheita) {
        ColheitaResumoDTO dto = new ColheitaResumoDTO();
        dto.setLote(colheita.getLote().getNome());
        dto.setProducaoBruta(colheita.getProducaoBruta());
        dto.setPrecoVenda(colheita.getPrecoAlqueire());
        dto.setReceitaLiquida(colheita.getSaldo());
        return dto;
    }
}