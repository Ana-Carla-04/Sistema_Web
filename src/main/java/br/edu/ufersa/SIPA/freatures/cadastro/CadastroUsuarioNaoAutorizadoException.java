package br.edu.ufersa.SIPA.freatures.cadastro;

import br.edu.ufersa.SIPA.shared.exeception.ConflictException;

public class CadastroUsuarioNaoAutorizadoException extends ConflictException {

    public CadastroUsuarioNaoAutorizadoException() {
        super("Usuário autenticado não corresponde ao recurso solicitado.");
    }

    public CadastroUsuarioNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}