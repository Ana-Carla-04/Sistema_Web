package br.edu.ufersa.SIPA.freatures.colheita.exception;

import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;

// Lançada quando a Colheita não existe, ou existe mas não pertence ao
// usuário autenticado (findByIdAndUsuarioId não encontrou nada).
// Ao herdar de ResourceNotFoundException, o GlobalExceptionHandler já
// sabe tratá-la e devolve 404 automaticamente, sem precisar de handler novo.
public class ColheitaNotFoundException extends ResourceNotFoundException {

    public ColheitaNotFoundException(Long id) {
        super("Colheita não encontrada: " + id);
    }

    public ColheitaNotFoundException(String mensagem) {
        super(mensagem);
    }
}