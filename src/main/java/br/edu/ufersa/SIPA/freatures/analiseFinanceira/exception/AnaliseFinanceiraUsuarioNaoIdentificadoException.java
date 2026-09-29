package br.edu.ufersa.SIPA.freatures.analiseFinanceira;

import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;

// Lançada pelo AnaliseFinanceiraApplicationService quando não é possível
// identificar o usuário autenticado que está pedindo a análise financeira.
public class AnaliseFinanceiraUsuarioNaoIdentificadoException extends UnauthorizedException {

    public AnaliseFinanceiraUsuarioNaoIdentificadoException(String mensagem) {
        super(mensagem);
    }
}
