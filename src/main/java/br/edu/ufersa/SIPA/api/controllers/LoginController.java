package br.edu.ufersa.SIPA.api.controllers;

//Login e Cadastro - Autenticação

import br.edu.ufersa.SIPA.domain.entities.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// essa anotação diz que essa classe vai receber requisições HTTP e devolver no formato JSON/XML(dados, nao paginas)
@RestController      //padrao do spring boot
@RequestMapping("/SIPA/login")   //define uma URL base para as funções deste controller
public class LoginController {

    // ---------- DTOs ----------
    // DTOs de entrada: só existem os campos que o usuário realmente informa.
    // Não há "id" nem qualquer outro campo sensível aqui, então não tem como
    // o cliente enviar isso no corpo da requisição (evita over-posting).
    // As anotações de Bean Validation (@NotBlank, @Email, @Size) validam os
    // dados antes de qualquer lógica de negócio rodar.

    public record CadastroRequestDTO(
        @NotBlank(message = "O nome é obrigatório") String nome,
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "Informe um e-mail válido") String email,
        @NotBlank(message = "A senha é obrigatória") @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String senha
    ) {}

    public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "Informe um e-mail válido") String email,
        @NotBlank(message = "A senha é obrigatória") String senha
    ) {}

    // DTO de saída: nunca devolve a senha (nem o hash) para o cliente.
    public record UsuarioResponseDTO(Long id, String nome, String email) {
        public static UsuarioResponseDTO fromEntity(Usuario usuario) {
            return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
        }
    }

    // ---------- Endpoints ----------

    /**
     * Cadastro de um novo usuário.
     * TODO: esta etapa ainda depende de um UsuarioRepository (interface
     * estendendo JpaRepository<Usuario, Long>), que ainda não existe no
     * projeto. Quando ele for criado, injete-o pelo construtor deste
     * controller e substitua o UnsupportedOperationException abaixo por:
     * 1) checar se já existe usuário com esse e-mail (409 se existir);
     * 2) gerar o hash da senha (nunca gravar senha em texto puro);
     * 3) salvar e devolver o UsuarioResponseDTO.
     */
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody CadastroRequestDTO dto) {
        throw new UnsupportedOperationException("Cadastro ainda não implementado: falta o UsuarioRepository");
    }

    /**
     * Login: autentica e grava o id do usuário na sessão HTTP.
     * TODO: comparar o e-mail/senha com o que estiver salvo (via
     * UsuarioRepository), e, se bater, gravar o id do usuário na sessão
     * (session.setAttribute("usuarioId", usuario.getId())). É esse id salvo
     * na sessão — nunca um valor vindo do cliente — que os controllers de
     * Plantio e Custo devem usar depois para validar a quem cada recurso
     * pertence (validação contextual / prevenção de IDOR).
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        throw new UnsupportedOperationException("Login ainda não implementado: falta o UsuarioRepository");
    }

    /**
     * Logout: encerra a sessão do usuário.
     * Usamos @DeleteMapping (e não POST) porque, semanticamente, o login
     * "cria" uma sessão (POST) e o logout "remove" essa sessão (DELETE).
     * GET, PUT e PATCH não têm uso neste controller por enquanto (não há,
     * por exemplo, edição de perfil ainda).
     */
    @DeleteMapping
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}