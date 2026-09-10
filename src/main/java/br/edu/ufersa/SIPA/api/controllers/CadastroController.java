package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.domain.entities.Usuario;
import org.springframework.web.bind.annotation.*;

// Ainda sem repositório ligado - métodos ainda retornando null
@RestController
@RequestMapping("/api/v1/usuarios")
public class CadastroController {

    @PostMapping
    public Usuario cadastrar(@RequestBody Usuario usuario) {
        return null;
    }

    @GetMapping("/{usuarioId}")
    public Usuario buscarPorId(@PathVariable Long usuarioId) {
        return null;
    }

    @PutMapping("/{usuarioId}")
    public Usuario atualizar(@PathVariable Long usuarioId, @RequestBody Usuario usuario) {
        return null;
    }

    @DeleteMapping("/{usuarioId}")
    public void excluir(@PathVariable Long usuarioId) {
    }
}