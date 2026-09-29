package br.edu.ufersa.SIPA.freatures.dashboard;

import br.edu.ufersa.SIPA.shared.exeception.UnauthorizedException;

// Lançada pelo DashboardApplicationService quando não é possível identificar
// o usuário autenticado que está pedindo o resumo do dashboard.
public class DashboardUsuarioNaoIdentificadoException extends UnauthorizedException {

    public DashboardUsuarioNaoIdentificadoException(String mensagem) {
        super(mensagem);
    }
}
