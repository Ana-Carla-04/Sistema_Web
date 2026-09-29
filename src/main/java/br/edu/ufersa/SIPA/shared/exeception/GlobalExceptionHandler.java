package br.edu.ufersa.SIPA.shared.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // ERROS DE VALIDAÇÃO (400)
    // ==========================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Um ou mais campos estão inválidos.");
        Map<String, String> erros = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> erros.put(error.getField(), error.getDefaultMessage()));
        problem.setProperty("erros", erros);
        return problem;
    }

    // ==========================================
    // RECURSO NÃO ENCONTRADO (404)
    // ==========================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail tratarNaoEncontrado(ResourceNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    // ==========================================
    // CONFLITO (409) — ex: email duplicado
    // ==========================================
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail tratarConflito(ConflictException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    // ==========================================
    // NÃO AUTENTICADO (401)
    // ==========================================
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail tratarNaoAutorizado(UnauthorizedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    // ==========================================
    // SEM PERMISSÃO (403)
    // ==========================================
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail tratarAcessoNegado(AccessDeniedException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Você não tem permissão para acessar este recurso.");
    }

    // ==========================================
    // CREDENCIAIS INVÁLIDAS (401)
    // ==========================================
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail tratarCredenciaisInvalidas(BadCredentialsException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Email ou senha incorretos.");
    }

    // ==========================================
    // ERRO INTERNO (500) — fallback
    // ==========================================
    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInterno(Exception exception) {
        // ⚠️ Em produção, logue a exceção completa:
        // log.error("Erro interno não tratado", exception);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor.");
    }
}