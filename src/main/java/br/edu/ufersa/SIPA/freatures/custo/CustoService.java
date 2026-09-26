package br.edu.ufersa.SIPA.freatures.custo;

import br.edu.ufersa.SIPA.freatures.custo.dto.CustoRequestDTO;
import br.edu.ufersa.SIPA.freatures.plantio.Plantio;
import br.edu.ufersa.SIPA.freatures.plantio.PlantioRepository;

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

    // Validação contextual (anti-IDOR)


    private Plantio validarPlantioDoUsuario(Long plantioId, Long usuarioId) {
        return plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plantio não encontrado"));
    }


    private Custo buscarCustoValido(Long plantioId, Long custoId, Long usuarioId) {
        return custoRepository
                .findByIdAndPlantioIdAndPlantioUsuarioId(custoId, plantioId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Custo não encontrado"));
    }

    //  Operações

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


        return custoRepository.save(custo);
    }

    @Transactional
    public void deletar(Long plantioId, Long custoId, Long usuarioId) {
        Custo custo = buscarCustoValido(plantioId, custoId, usuarioId);
        custoRepository.delete(custo);
     }
}