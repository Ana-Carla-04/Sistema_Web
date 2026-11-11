package br.edu.ufersa.SIPA.shared.exeception;

// Lançada quando o request chegou autenticado (passou pelo SecurityFilter),
// mas por algum motivo o usuário não pôde ser identificado ou não existe
// mais no banco (ex.: token válido de um usuário já excluído).
// É um problema de autenticação, não de "recurso não encontrado" nem de
// "conflito" — por isso tem sua própria hierarquia, mapeada para 401 no
// GlobalExceptionHandler.
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String mensagem) {
        super(mensagem);
    }
}
