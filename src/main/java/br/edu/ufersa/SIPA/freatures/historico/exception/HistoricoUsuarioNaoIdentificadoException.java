package br.edu.ufersa.SIPA.freatures.historico.exception;

import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;

// Lançada pelo HistoricoApplicationService quando não é possível identificar
// o usuário autenticado que está pedindo o histórico de safras.
public class HistoricoUsuarioNaoIdentificadoException extends UnauthorizedException {

    public HistoricoUsuarioNaoIdentificadoException(String mensagem) {
        super(mensagem);
    }
}
