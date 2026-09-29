package br.edu.ufersa.SIPA.freatures.colheita;

import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;

// Lançada pelo ColheitaService quando não é possível identificar
// o usuário autenticado que está pedindo dados de colheita.
public class ColheitaUsuarioNaoIdentificadoException extends UnauthorizedException {

    public ColheitaUsuarioNaoIdentificadoException(String mensagem) {
        super(mensagem);
    }
}