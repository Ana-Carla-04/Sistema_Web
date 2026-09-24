@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // erros de validação de jakarta bean validation (@valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public PorblemDetail tratarValidacao(MethodArgumentNotValidExcepion ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST, "um ou mais campos estão inválidos. corrija e tente novamente.");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Erro de validação de dados de entrada");
        problemDetail.setProperties("timestamp", LocalDateTime.now());
        Map<String, String> erros = new HashMap<>();
        for (fieldError fe : ex.getBindingResult().getFieldErrors()) {
            camposComErros.put(fe.getField(), fe.getDefaultMessage());
        }
        problemDetail.setProperties("erros",camposComErros);
        return problemDetail;
    }
    // violação de regra de negocio/duplicidade (HTTP 422 unprocessable entity)
    @ExceptionHandler(OperacaoInvalidaException.class)
    public ProblemDetail tratarOperacaoInvalida(OperacaoInvalidaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Violação de regra de negócio");
        problemDetail.setProperties("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    // recurso não encontrado (HTTP 404 not found)
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail tratarEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setProperties("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    // erro interno do servidor (HTTP 500 internal server error)
    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInterno(Exception ex) {
        logger.error("Erro interno do servidor", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Erro interno do servidor");
        problemDetail.setProperties("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    // erro de autenticação (HTTP 401 unauthorized)
    @ExceptionHandler(AutenticacaoException.class)
    public ProblemDetail tratarAutenticacao(AutenticacaoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Erro de autenticação");
        problemDetail.setProperties("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    // erro de autorização (HTTP 403 forbidden)
    @ExceptionHandler(AutorizacaoException.class)
    public ProblemDetail tratarAutorizacao(AutorizacaoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Erro de autorização");
        problemDetail.setProperties("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    
}
