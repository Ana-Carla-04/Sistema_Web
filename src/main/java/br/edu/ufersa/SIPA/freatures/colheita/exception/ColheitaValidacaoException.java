package br.edu.ufersa.SIPA.freatures.colheita;

import br.edu.ufersa.SIPA.shared.exeception.ConflictException;

// Lançada quando os dados da colheita são inválidos:
// produção negativa, desconto de umidade fora do intervalo, etc.
public class ColheitaValidacaoException extends ConflictException {

    public ColheitaValidacaoException(String mensagem) {
        super(mensagem);
    }

    public static ColheitaValidacaoException producaoInvalida(Double producao) {
        return new ColheitaValidacaoException(
                "A produção bruta deve ser maior que zero. Valor recebido: " + producao);
    }

    public static ColheitaValidacaoException descontoUmidadeInvalido(Double desconto) {
        return new ColheitaValidacaoException(
                "O desconto de umidade deve estar entre 0 e 100. Valor recebido: " + desconto);
    }

    public static ColheitaValidacaoException precoInvalido(Double preco) {
        return new ColheitaValidacaoException(
                "O preço por alqueire deve ser maior que zero. Valor recebido: " + preco);
    }

    public static ColheitaValidacaoException taxaMaquinaInvalida(Double taxa) {
        return new ColheitaValidacaoException(
                "A taxa da máquina deve estar entre 0 e 100. Valor recebido: " + taxa);
    }

    public static ColheitaValidacaoException dataFutura() {
        return new ColheitaValidacaoException(
                "A data da colheita não pode ser no futuro.");
    }
}