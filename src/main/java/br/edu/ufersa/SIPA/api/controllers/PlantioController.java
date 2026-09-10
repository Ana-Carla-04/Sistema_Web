package br.edu.ufersa.SIPA.api.controllers;

import br.edu.ufersa.SIPA.domain.entities.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// PlantioController - Tela de Plantios

// GET - Para OBTER/VER dados
// POST - Para CRIAR/ADICIONAR dados
// PUT - Para ATUALIZAR/SUBSTITUIR dados
// PATCH - Para ATUALIZAR PARCIALMENTE dados
// DELETE - Para REMOVER/EXCLUIR dados

// Essa anotação diz que essa classe vai receber requisições HTTP e devolver no formato JSON/XML (dados, não páginas)
@RestController      // padrao do spring boot
@RequestMapping("/SIPA/{userId}/plantio")   // define uma URL e o método HTTP que vai executar cada função
public class PlantioController {

    @GetMapping("/testes")
    public String testar() {
        return "Primeiro endpoint criado!!";
    }

    // lista todos os plantios
    // @PathVariable pega um valor que está dentro da URL e coloca esse valor no parametro do metodo.
    // Neste caso, {userId} vem do @RequestMapping e identifica o usuario dos plantios.
    // Exemplo: na URL /SIPA/10/plantio, o valor 10 será recebido em userId.
    @GetMapping
    public ResponseEntity<List<Plantio>> listarTodos(@PathVariable Long userId) {
        return null;
    }

    // listar plantio por data
    @GetMapping("/data")
    public ResponseEntity<List<Plantio>> listarData(@PathVariable Long userId, @RequestParam LocalDate data) {
        return null;
    }

    // listar plantio por status
    @GetMapping("/status")
    public ResponseEntity<List<Plantio>> listarStatus(@PathVariable Long userId, @RequestParam String status) {
        return null;
    }

    // listar plantio por nome
    @GetMapping("/nomePlantio")
    public ResponseEntity<List<Plantio>> listarNome(@PathVariable Long userId, @RequestParam String nome) {
        return null;
    }

    // tela de sobreposição editar plantio:
    // editar plantio (atualizar dados do plantio)
    // @PutMapping representa uma requisicao HTTP PUT.
    // E usado para atualizar ou substituir todos os dados já existentes.
    // O caminho final fica, por exemplo: PUT /SIPA/10/plantio/25
    // Nesse exemplo, 10 e o usuarioId e 25 e o ID.
    // O @RequestBody recebe a nova versao completa da tarefa.
    // O @PathVariable userId recebe o 10 e o @PathVariable id recebe o 25.
    // Cada nome entre chaves na URL deve corresponder a um @PathVariable do metodo.
    @PutMapping("/editar/{id}")
    public ResponseEntity<Plantio> editarPlantio(@PathVariable Long userId, @PathVariable Long id, @RequestBody Plantio plantio) {
        return null;
    }

    // deletar plantio
    // @DeleteMapping representa uma requisicao HTTP DELETE.
    // E usado para remover uma tarefa existente.
    // O caminho fica, por exemplo: DELETE /SIPA/10/plantio/25
    // O usuarioId identifica o dono e o tarefaId identifica a tarefa que sera removida.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletarPlantio(@PathVariable Long userId, @PathVariable Long id) {
        return null;
    }

    // tela de sobreposição de adicionar plantio:
    // adicionar plantio (adicionar dados de plantio)
    // @PostMapping representa uma requisicao HTTP POST.
    // E usado para criar uma nova tarefa.
    // Como nao existe um caminho adicional dentro da anotacao, o endpoint fica:
    // POST /SIPA/{usuarioId}/plantio
    // Cada nome entre chaves na URL deve corresponder a um @PathVariable do metodo.
    @PostMapping("/adicionar")
    public ResponseEntity<Plantio> adicionarPlantio(@PathVariable Long userId, @RequestBody Plantio plantio) {
        return null;
    }
}