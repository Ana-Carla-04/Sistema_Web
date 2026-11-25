package br.edu.ufersa.SIPA.freatures.tarefa.exception;

import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;

// Lançada quando a Tarefa não existe, ou existe mas não pertence ao
// usuário autenticado (findByIdAndUsuarioId não encontrou nada).
public class TarefaNotFoundException extends ResourceNotFoundException {

    public TarefaNotFoundException(Long id) {
        super("Tarefa não encontrada: " + id);
    }

    public TarefaNotFoundException(String mensagem) {
        super(mensagem);
    }
}
