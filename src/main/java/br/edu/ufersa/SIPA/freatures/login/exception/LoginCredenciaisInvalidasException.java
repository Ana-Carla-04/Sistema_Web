package br.edu.ufersa.SIPA.freatures.login;

import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;

public class LoginCredenciaisInvalidasException extends UnauthorizedException {

    public LoginCredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }

    public LoginCredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}