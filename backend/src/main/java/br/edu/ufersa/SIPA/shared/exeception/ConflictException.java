package br.edu.ufersa.SIPA.shared.exeception;

// Lançada quando a operação conflita com um dado já existente

public class ConflictException extends RuntimeException {
    public ConflictException(String mensagem) {

        super(mensagem);
    }
}