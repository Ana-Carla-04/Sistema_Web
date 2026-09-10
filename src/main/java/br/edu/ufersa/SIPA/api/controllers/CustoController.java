package br.edu.ufersa.SIPA.api.controllers;

//CustoController - Tela de Custos

import br.edu.ufersa.SIPA.domain.repositories.CustoRepository;
import br.edu.ufersa.SIPA.domain.repositories.PlantioRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

//GET    - Para OBTER/VER dados
//POST   - Para CRIAR/ADICIONAR dados
//PUT    - Para ATUALIZAR/SUBSTITUIR dados
//DELETE - Para REMOVER/EXCLUIR dados
//(PATCH não foi incluído: ainda não existe um caso de atualização parcial
// diferente do PUT para custo — pode ser adicionado depois, se surgir essa
// necessidade, ex.: reanexar só o comprovante.)

//essa anotação diz que essa classe vai receber requisições HTTP e devolver no formato JSON/XML(dados, nao paginas)
@RestController      //padrao do spring boot
//Rota hierárquica (/SIPA/plantios/{plantioId}/custos), em vez de uma rota
//"achatada" (/custos/{id}): assim, para acessar qualquer custo é obrigatório
//informar também o plantio "pai" na própria URL. É isso que permite checar,
//na validação contextual, se aquele custo realmente pertence àquele plantio
//e se o plantio pertence a quem está logado — prevenindo IDOR (o usuário
//trocar o id na URL para acessar/alterar um custo que não é dele).
@RequestMapping("/SIPA/plantios/{plantioId}/custos")
public class CustoController {

    private final CustoRepository custoRepository;
    private final PlantioRepository plantioRepository;

    public CustoController(CustoRepository custoRepository, PlantioRepository plantioRepository) {
        this.custoRepository = custoRepository;
        this.plantioRepository = plantioRepository;
    }

    // ---------- DTOs ----------

    // DTO de entrada. Não existe "plantioId" aqui de propósito: o plantio já
    // vem da própria URL (@PathVariable), então o cliente não pode "forjar"
    // no JSON um plantioId de outro produtor (over-posting). As anotações
    // @NotNull/@NotBlank/@Positive aplicam o Bean Validation nos dados.
    public record CustoRequestDTO(
        @NotNull(message = "A data do custo é obrigatória") LocalDate data,
        @NotBlank(message = "A categoria é obrigatória") String categoria,
        String descricao,
        @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser maior que zero") Double valor,
        String comprovante
    ) {}

    public record CustoResponseDTO(
        Long id,
        Long plantioId,
        LocalDate data,
        String categoria,
        String descricao,
        Double valor,
        String comprovante
    ) {}

    // ---------- Validação contextual (anti-IDOR) ----------

    /**
     * Recupera o id do usuário logado a partir da sessão HTTP — nunca de um
     * @RequestParam, @PathVariable ou do corpo do JSON. Depende do
     * LoginController gravar "usuarioId" na sessão no momento do login
     * (ainda pendente, ver TODO em LoginController.login).
     */
    private Long obterUsuarioLogado(HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        return (Long) usuarioId;
    }

    /**
     * TODO: assim que PlantioRepository virar uma interface JpaRepository
     * (com Plantio anotado @Entity e relacionado a Usuario), trocar este
     * método para usar algo como:
     * plantioRepository.findByIdAndUsuarioId(plantioId, usuarioId)
     * .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantio não encontrado"));
     * Isso garante que só se acessa custos de um plantio que pertence ao
     * usuário logado.
     */
    private void validarPlantioDoUsuario(Long plantioId, Long usuarioId) {
        throw new UnsupportedOperationException("Validação de plantio ainda não implementada: falta o PlantioRepository");
    }

    // ---------- Endpoints ----------

    @GetMapping
    public List<CustoResponseDTO> listar(@PathVariable Long plantioId, HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        validarPlantioDoUsuario(plantioId, usuarioId);
        // TODO: return custoRepository.findByPlantioIdAndPlantioUsuarioId(plantioId, usuarioId)...
        throw new UnsupportedOperationException("Listagem de custos ainda não implementada: falta o CustoRepository");
    }

    @GetMapping("/{custoId}")
    public CustoResponseDTO buscarPorId(@PathVariable Long plantioId, @PathVariable Long custoId, HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        // TODO: custoRepository.findByIdAndPlantioIdAndPlantioUsuarioId(custoId, plantioId, usuarioId)...
        throw new UnsupportedOperationException("Busca de custo ainda não implementada: falta o CustoRepository");
    }

    @PostMapping
    public ResponseEntity<CustoResponseDTO> criar(@PathVariable Long plantioId,
                                                   @Valid @RequestBody CustoRequestDTO dto,
                                                   HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        validarPlantioDoUsuario(plantioId, usuarioId);
        // TODO: montar o Custo a partir do dto + plantio validado e salvar no custoRepository
        throw new UnsupportedOperationException("Criação de custo ainda não implementada: falta o CustoRepository");
    }

    @PutMapping("/{custoId}")
    public CustoResponseDTO atualizar(@PathVariable Long plantioId,
                                       @PathVariable Long custoId,
                                       @Valid @RequestBody CustoRequestDTO dto,
                                       HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        // TODO: buscar o custo validando plantio+usuário, atualizar os campos e salvar
        throw new UnsupportedOperationException("Atualização de custo ainda não implementada: falta o CustoRepository");
    }

    @DeleteMapping("/{custoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long plantioId, @PathVariable Long custoId, HttpSession session) {
        Long usuarioId = obterUsuarioLogado(session);
        // TODO: buscar o custo validando plantio+usuário e remover
        throw new UnsupportedOperationException("Remoção de custo ainda não implementada: falta o CustoRepository");
    }
}