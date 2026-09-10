package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.domain.entities.Tarefa;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/{usuarioId}/tarefas")
public class TarefaController {

    @GetMapping
    public List<Tarefa> listar(@PathVariable Long usuarioId) {
        return null;
    }

    @GetMapping("/{tarefaId}")
    public Tarefa buscarPorId(@PathVariable Long usuarioId, @PathVariable Long tarefaId) {
        return null;
    }

    @PostMapping
    public Tarefa criar(@PathVariable Long usuarioId, @RequestBody Tarefa tarefa) {
        return null;
    }

    @PutMapping("/{tarefaId}")
    public Tarefa atualizar(@PathVariable Long usuarioId, @PathVariable Long tarefaId, @RequestBody Tarefa tarefa) {
        return null;
    }

    @PatchMapping("/{tarefaId}")
    public Tarefa concluir(@PathVariable Long usuarioId, @PathVariable Long tarefaId) {
        return null;
    }

    @DeleteMapping("/{tarefaId}")
    public void remover(@PathVariable Long usuarioId, @PathVariable Long tarefaId) {
    }
}