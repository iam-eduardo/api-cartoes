package com.cartoes.api_cartoes.application.exception;

import com.cartoes.api_cartoes.application.dto.response.ProblemDetail;
import com.cartoes.api_cartoes.domain.exception.BusinessException;
import com.cartoes.api_cartoes.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String APP_NAME = "cartoes-api";
    private static final String BASE_ERROR_TYPE = "https://api.cartoes.com/problems";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Erro não esperado: ", ex);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("app", APP_NAME);
        extensions.put("tipoErro", "SERVICO_INDISPONIVEL");
        extensions.put("mensagemInterna", "Tivemos um problema, mas fique tranquilo que nosso time já foi avisado.");

        ProblemDetail problem = ProblemDetail.builder()
                .type(BASE_ERROR_TYPE + "/internal-server-error")
                .title("Erro Interno do Servidor")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("Um erro inesperado ocorreu.")
                .instance("/errors/" + UUID.randomUUID())
                .extensions(extensions)
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
        log.error("Erro de negócio: ", ex);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("app", APP_NAME);
        extensions.put("tipoErro", "REGRA_NEGOCIO");
        extensions.put("codigo", "422");

        ProblemDetail problem = ProblemDetail.builder()
                .type(BASE_ERROR_TYPE + "/business-rule-violation")
                .title("Violação de Regra de Negócio")
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .detail(ex.getMessage())
                .instance("/errors/" + UUID.randomUUID())
                .extensions(extensions)
                .build();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Erro de validação: ", ex);

        String errorDetail = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("app", APP_NAME);
        extensions.put("tipoErro", "VALIDACAO");
        extensions.put("codigo", "400");
        extensions.put("errors", ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                )));

        ProblemDetail problem = ProblemDetail.builder()
                .type(BASE_ERROR_TYPE + "/validation-error")
                .title("Erro de Validação")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("Requisição inválida: " + errorDetail)
                .instance("/errors/" + UUID.randomUUID())
                .extensions(extensions)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Recurso não encontrado: ", ex);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("app", APP_NAME);
        extensions.put("tipoErro", "RECURSO_NAO_ENCONTRADO");
        extensions.put("codigo", "404");

        ProblemDetail problem = ProblemDetail.builder()
                .type(BASE_ERROR_TYPE + "/resource-not-found")
                .title("Recurso Não Encontrado")
                .status(HttpStatus.NOT_FOUND.value())
                .detail(ex.getMessage())
                .instance("/errors/" + UUID.randomUUID())
                .extensions(extensions)
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
