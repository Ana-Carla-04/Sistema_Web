package br.edu.ufersa.SIPA.freatures.custo;

import br.edu.ufersa.SIPA.freatures.custo.dto.CustoRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioNotFoundException;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ==========================================
    // VALIDAÇÕES (anti-IDOR)
    // ==========================================

    private Plantio validarPlantioDoUsuario(Long plantioId, Long usuarioId) {
        return plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new PlantioNotFoundException(plantioId));
    }

    private Custo buscarCustoValido(Long custoId, Long usuarioId) {
        return custoRepository.findByIdAndPlantioUsuarioId(custoId, usuarioId)
                .orElseThrow(() -> new CustoNotFoundException(custoId));
    }

    // GET /SIPA/custos
    @Transactional(readOnly = true)
    public List<Custo> listarTodos(Long usuarioId) {
        return custoRepository.findByPlantioUsuarioId(usuarioId);
    }

    // GET /SIPA/custos/plantios/{plantioId}
    @Transactional(readOnly = true)
    public List<Custo> listar(Long plantioId, Long usuarioId) {
        validarPlantioDoUsuario(plantioId, usuarioId);
        return custoRepository.findByPlantioIdAndPlantioUsuarioId(plantioId, usuarioId);
    }

    // GET /SIPA/custos/{custoId}
    @Transactional(readOnly = true)
    public Custo buscarPorId(Long custoId, Long usuarioId) {
        return buscarCustoValido(custoId, usuarioId);
    }

    // POST /SIPA/custos/plantios/{plantioId}
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

    // PUT /SIPA/custos/{custoId}
    @Transactional
    public Custo atualizar(Long custoId, CustoRequestDTO dto, Long usuarioId) {
        Custo custo = buscarCustoValido(custoId, usuarioId);

        custo.setData(dto.data());
        custo.setCategoria(dto.categoria());
        custo.setDescricao(dto.descricao());
        custo.setValor(dto.valor());
        custo.setComprovante(dto.comprovante());

        return custoRepository.save(custo);
    }

    // DELETE /SIPA/custos/{custoId}
    @Transactional
    public void deletar(Long custoId, Long usuarioId) {
        Custo custo = buscarCustoValido(custoId, usuarioId);
        custoRepository.delete(custo);
    }
}