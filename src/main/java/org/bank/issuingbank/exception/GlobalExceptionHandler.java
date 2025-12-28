package org.bank.issuingbank.exception;

import org.bank.issuingbank.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de negocio personalizadas
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request) {

        String correlationId = MDC.get("correlationId");
        String path = request.getDescription(false).replace("uri=", "");

        // Log de error de negocio (nivel WARN porque es un error controlado)
        log.warn("Business exception occurred - correlationId: {}, path: {}, errorCode: {}, message: {}",
                correlationId, path, ex.getErrorCode(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de validación de entrada
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String correlationId = MDC.get("correlationId");
        String path = request.getDescription(false).replace("uri=", "");

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        // Log de error de validación
        log.warn("Validation error - correlationId: {}, path: {}, errors: {}",
                correlationId, path, message);

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                message,
                HttpStatus.BAD_REQUEST.value(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones generales no capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        String correlationId = MDC.get("correlationId");
        String path = request.getDescription(false).replace("uri=", "");

        // Log de error crítico con stack trace completo
        log.error("Unexpected exception occurred - correlationId: {}, path: {}, exception: {}",
                correlationId, path, ex.getClass().getName(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please contact support with correlationId: " + correlationId,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja errores de recurso no encontrado
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        String correlationId = MDC.get("correlationId");
        String path = request.getDescription(false).replace("uri=", "");

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorCode = "RUNTIME_ERROR";

        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            errorCode = "NOT_FOUND";
            
            // Log de recurso no encontrado (WARN porque es esperado)
            log.warn("Resource not found - correlationId: {}, path: {}, message: {}",
                    correlationId, path, ex.getMessage());
        } else {
            // Log de error runtime no controlado
            log.error("Runtime exception - correlationId: {}, path: {}, message: {}",
                    correlationId, path, ex.getMessage(), ex);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                status.value(),
                path
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
