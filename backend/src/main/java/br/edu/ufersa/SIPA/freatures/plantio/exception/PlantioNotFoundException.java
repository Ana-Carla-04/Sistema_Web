package br.edu.ufersa.SIPA.freatures.plantio.exception;

import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;

// Lançada quando o Plantio (lote) não existe, ou existe mas não pertence
// ao usuário autenticado (findByIdAndUsuarioId não encontrou nada).
// Ao herdar de ResourceNotFoundException, o GlobalExceptionHandler já
// sabe tratá-la e devolve 404 automaticamente, sem precisar de handler novo.
public class PlantioNotFoundException extends ResourceNotFoundException {

    public PlantioNotFoundException(Long id) {
        super("Plantio não encontrado para este usuário: " + id);
    }

    public PlantioNotFoundException(String mensagem) {
        super(mensagem);
    }
}
