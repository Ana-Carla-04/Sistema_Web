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
    @PutMapping("/editar/{id}")
    public ResponseEntity<Plantio> editarPlantio(@PathVariable Long userId, @PathVariable Long id, @RequestBody Plantio plantio) {
        return null;
    }

    // deletar plantio
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletarPlantio(@PathVariable Long userId, @PathVariable Long id) {
        return null;
    }

    // tela de sobreposição de adicionar plantio:
    // adicionar plantio (adicionar dados de plantio)
    @PostMapping("/adicionar")
    public ResponseEntity<Plantio> adicionarPlantio(@PathVariable Long userId, @RequestBody Plantio plantio) {
        return null;
    }
}