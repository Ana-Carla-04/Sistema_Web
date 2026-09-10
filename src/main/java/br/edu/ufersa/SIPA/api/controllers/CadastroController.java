package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.domain.entities.Usuario;
import org.springframework.web.bind.annotation.*;

// Ainda sem repositório ligado - métodos ainda retornando null
@RestController
@RequestMapping("/SIPA/usuarios")
public class CadastroController {

    // @PostMapping representa uma requisicao HTTP POST.
    // E usado para criar um novo cadastro de usuario.
    // Como nao ha caminho adicional, o endpoint fica: POST /SIPA/usuarios
    // O @RequestBody recebe os dados do usuario no corpo da requisicao.
    @PostMapping
    public Usuario cadastrar(@RequestBody Usuario usuario) {
        return null;
    }

    @GetMapping("/{usuarioId}")
    public Usuario buscarPorId(@PathVariable Long usuarioId) {
        return null;
    }

    // @PutMapping representa uma requisicao HTTP PUT.
    // E usado para atualizar ou substituir todos os dados do usuario.
    // Exemplo: PUT /SIPA/usuarios/10.
    // O @PathVariable usuarioId recebe o valor 10 da URL.
    // O @RequestBody recebe a versao completa e atualizada do usuario.
    @PutMapping("/{usuarioId}")
    public Usuario atualizar(@PathVariable Long usuarioId, @RequestBody Usuario usuario) {
        return null;
    }

    @DeleteMapping("/{usuarioId}")
    public void excluir(@PathVariable Long usuarioId) {
    }
}