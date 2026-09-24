package com.example.Football_League_API.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralizador global de exceções da API.
 * Intercepta exceções lançadas em qualquer @RestController e retorna respostas
 * padronizadas com HTTP status apropriado (404, 400, 409, 500, etc).
 * Cada método trata um tipo específico de erro e estrutura a resposta em JSON.
 */
@RestControllerAdvice // Aplica este handler a todos os controllers
public class GlobalExceptionHandler {

    /**
     * Trata EntityNotFoundException (recurso não encontrado).
     * Retorna HTTP 404 quando alguém tenta acessar um ID que não existe.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Quando ocorreu o erro
        body.put("status", HttpStatus.NOT_FOUND.value()); // 404
        body.put("message", ex.getMessage()); // Mensagem customizada (ex: "Campeonato não encontrado com ID: 999")
        body.put("path", request.getDescription(false).replace("uri=", "")); // Qual endpoint foi chamado
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Trata validação de campos (Bean Validation).
     * Retorna HTTP 400 quando os dados enviados não atendem validações (@NotNull, @NotBlank, etc).
     * Detalha qual campo falhou e por quê.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        body.put("message", "Validação falhou");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Extrai erros por campo e mapeia para resposta
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()) // Ex: {"nome": "não pode ser vazio"}
        );
        body.put("errors", errors); // Lista quais campos falharam na validação

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata violação de constraints únicos do banco de dados.
     * Retorna HTTP 409 CONFLICT quando há duplicação (ex: mesmo nome de campeonato,
     * mesmo time na mesma temporada, etc).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); // 409 - Conflito
        body.put("message", "Violação de constraint: verifique dados duplicados ou relacionamentos");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * Fallback: trata qualquer exceção não capturada pelos handlers acima.
     * Retorna HTTP 500 para erros inesperados, mantendo segurança (sem expor stacktrace).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        body.put("message", "Erro interno do servidor");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
