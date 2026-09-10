package br.edu.ufersa.SIPA.api.services;

import br.edu.ufersa.SIPA.api.dtos.CustoRequestDTO;
import br.edu.ufersa.SIPA.domain.entities.Custo;
import br.edu.ufersa.SIPA.domain.entities.Plantio;
import br.edu.ufersa.SIPA.domain.repositories.CustoRepository;
import br.edu.ufersa.SIPA.domain.repositories.PlantioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustoService {

    private final CustoRepository custoRepository;
    private final PlantioRepository plantioRepository;

    public CustoService(CustoRepository custoRepository,
                        PlantioRepository plantioRepository) {
        this.custoRepository = custoRepository;
        this.plantioRepository = plantioRepository;
    }

    // ---------- Validação contextual (anti-IDOR) ----------

    /** Verifica se o plantio existe E pertence ao usuário logado. */
    private Plantio validarPlantioDoUsuario(Long plantioId, Long usuarioId) {
        return plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plantio não encontrado"));
    }

    /** Busca o custo validando plantio + dono (anti-IDOR). */
    private Custo buscarCustoValido(Long plantioId, Long custoId, Long usuarioId) {
        return custoRepository
                .findByIdAndPlantioIdAndPlantioUsuarioId(custoId, plantioId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Custo não encontrado"));
    }

    // ---------- Operações ----------

    @Transactional(readOnly = true)
    public List<Custo> listar(Long plantioId, Long usuarioId) {
        validarPlantioDoUsuario(plantioId, usuarioId);
        return custoRepository.findByPlantioIdAndPlantioUsuarioId(plantioId, usuarioId);
    }

    @Transactional(readOnly = true)
    public Custo buscarPorId(Long plantioId, Long custoId, Long usuarioId) {
        return buscarCustoValido(plantioId, custoId, usuarioId);
    }

    @Transactional
    public Custo criar(Long plantioId, CustoRequestDTO dto, Long usuarioId) {
        Plantio plantio = validarPlantioDoUsuario(plantioId, usuarioId);

        Custo custo = new Custo();
        custo.setData(dto.data());
        custo.setCategoria(dto.categoria());
        custo.setDescricao(dto.descricao());
        custo.setValor(dto.valor());
        custo.setComprovante(dto.comprovante());
        custo.setPlantio(plantio);

        return custoRepository.save(custo);
    }

    @Transactional
    public Custo atualizar(Long plantioId, Long custoId,
                           CustoRequestDTO dto, Long usuarioId) {
        Custo custo = buscarCustoValido(plantioId, custoId, usuarioId);

        custo.setData(dto.data());
        custo.setCategoria(dto.categoria());
        custo.setDescricao(dto.descricao());
        custo.setValor(dto.valor());
        custo.setComprovante(dto.comprovante());
        // plantio NÃO muda: vem da URL e já foi validado

        return custoRepository.save(custo);
    }

    @Transactional
    public void deletar(Long plantioId, Long custoId, Long usuarioId) {
        Custo custo = buscarCustoValido(plantioId, custoId, usuarioId);
        custoRepository.delete(custo);
    }
}