package br.edu.ufersa.SIPA.api.controllers;

//Login e Cadastro - Autenticação

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//essa anotação diz que essa classe vai receber requisições HTTP e devolver no formato JSON/XML(dados, nao paginas)
@RestController      //padrao do spring boot
@RequestMapping   //define um URL e o metodo HTTP que vai executar cada função
public class LoginController {
}
