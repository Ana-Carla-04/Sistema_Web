package br.edu.ufersa.SIPA.freatures.auth.exception;

import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;

public class UsuarioNaoEncontradoException extends ResourceNotFoundException {

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado: " + id);
    }

    public UsuarioNaoEncontradoException(String email) {
        super("Usuário não encontrado: " + email);
    }
}