package br.edu.ufersa.SIPA.api.controllers;
import br.edu.ufersa.SIPA.domain.entities.*;

//PlantioController - Tela de Plantios


import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


//GET - Para OBTER/VER dados
//POST - Para CRIAR/ADICIONAR dados
//PUT - Para ATUALIZAR/SUBSTITUIR dados
//PATCH - Para ATUALIZAR PARCIALMENTE dados
//DELETE - Para REMOVER/EXCLUIR dados


//essa anotação diz que essa classe vai receber requisições HTTP e devolver no formato JSON/XML(dados, nao paginas)
@RestController      //padrao do spring boot
@RequestMapping("/SIPA/plantio")   //define um URL e o metodo HTTP que vai executar cada função
public class PlantioController {
    @GetMapping("/testes")
    public String testar(){
        return "Primeiro endpoint criado!!";
    }



    //lista todos os plantios
//    @GetMapping
//    public List<Plantio> listarTodos(){
//
//    }
//
//    //listar plantio por data
//    @GetMapping("/data")
//    public List<Plantio> listarData(@RequestParam LocalDate data){ //requisita o parametro data
//
//    }
//
//    //listar plantio por status
//    @GetMapping("/status")
//    public List<Plantio> listarStatus(@RequestParam String status){
//
//    }
//
//    //listar plantio por nome
//    @GetMapping("/nomePlantio")
//    public List<Plantio> listarNome(@RequestParam String nome){
//
//    }
//
//    //tela de sobreposição editar plantio:
//    //editar plantio(atualizar dados do plantio)
//    @PutMapping("/editar")
//    public Plantio editarPlantio(@RequestParam Long id, @RequestParam Plantio plantio){
//
//    }
//
//    //deletar plantio
//    @DeleteMapping("/delete")
//    public void deletarPlantio(@RequestParam Long id){
//
//    }
//
//    //tela de sobreposição de adicionar plantio:
//    //adicionar plantio(adicionar dados de plantio
//    @PostMapping("/adicionar")
//    public Plantio adicionarPlantio(@RequestParam Plantio plantio){
//
//    }

}
