package br.edu.ufersa.SIPA.freatures.custo;

import br.edu.ufersa.SIPA.shared.exeception.ResourceNotFoundException;

// Lançada quando o Custo não existe, ou existe mas não pertence ao plantio/
// usuário informado na rota (findByIdAndPlantioIdAndPlantioUsuarioId vazio).
public class CustoNotFoundException extends ResourceNotFoundException {

    public CustoNotFoundException(Long id) {
        super("Custo não encontrado: " + id);
    }

    public CustoNotFoundException(String mensagem) {
        super(mensagem);
    }
}