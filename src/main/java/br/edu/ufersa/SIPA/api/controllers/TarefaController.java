package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.domain.entities.Tarefa;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/SIPA/{usuarioId}/tarefas")
public class TarefaController {

    // @GetMapping representa uma requisicao HTTP GET.
    // E usado para consultar ou buscar dados, sem criar ou alterar uma tarefa.
    // Como nao existe um caminho adicional, esta rota lista as tarefas do usuario:
    // GET /SIPA/10/tarefas
    // O @PathVariable recebe o valor 10 e identifica o usuario consultado.
    @GetMapping
    public List<Tarefa> listar(@PathVariable Long usuarioId) {
        return null;
    }

    // Este segundo @GetMapping tambem faz uma consulta, mas busca uma tarefa especifica.
    // O caminho fica, por exemplo: GET /SIPA/10/tarefas/25
    // Nesse exemplo, 10 e o usuarioId e 25 e o tarefaId.
    @GetMapping("/{tarefaId}")
    public Tarefa buscarPorId(@PathVariable Long usuarioId, @PathVariable Long tarefaId) {
        return null;
    }

    // @PostMapping representa uma requisicao HTTP POST.
    // E usado para criar uma nova tarefa.
    // Como nao existe um caminho adicional dentro da anotacao, o endpoint fica:
    // POST /SIPA/{usuarioId}/tarefas
    // O @RequestBody recebe os dados da nova tarefa enviados no corpo da requisicao.
    @PostMapping
    public Tarefa criar(@PathVariable Long usuarioId, @RequestBody Tarefa tarefa) {
        return null;
    }

    // @PutMapping representa uma requisicao HTTP PUT.
    // E usado para atualizar ou substituir todos os dados de uma tarefa existente.
    // O caminho final fica, por exemplo: PUT /SIPA/10/tarefas/25
    // Nesse exemplo, 10 e o usuarioId e 25 e o tarefaId.
    // O @RequestBody recebe a nova versao completa da tarefa.
    @PutMapping("/{tarefaId}")
    public Tarefa atualizar(@PathVariable Long usuarioId, @PathVariable Long tarefaId, @RequestBody Tarefa tarefa) {
        return null;
    }

    // @PatchMapping representa uma requisicao HTTP PATCH.
    // E usado para alterar apenas uma parte da tarefa, sem substituir todos os dados.
    // Neste controller, o metodo concluir indica que a alteracao sera a conclusao da tarefa.
    // O caminho final fica, por exemplo: PATCH /SIPA/10/tarefas/25
    // O usuarioId e o tarefaId identificam qual tarefa sera alterada.
    @PatchMapping("/{tarefaId}")
    public Tarefa concluir(@PathVariable Long usuarioId, @PathVariable Long tarefaId) {
        return null;
    }

    // @DeleteMapping representa uma requisicao HTTP DELETE.
    // E usado para remover uma tarefa existente.
    // O caminho fica, por exemplo: DELETE /SIPA/10/tarefas/25
    // O usuarioId identifica o dono e o tarefaId identifica a tarefa que sera removida.
    // Como a operacao nao envia dados no corpo, o metodo recebe apenas os identificadores.
    @DeleteMapping("/{tarefaId}")
    public void remover(@PathVariable Long usuarioId, @PathVariable Long tarefaId) {
    }
}