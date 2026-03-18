package com.bike.shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 - Error de validación de negocio
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(ValidacionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 400 - Error de trigger MySQL (stock insuficiente, etc.)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(Exception ex) {
        String mensaje = ex.getMessage();
        // Extraer el mensaje del trigger MySQL si está disponible
        if (mensaje != null && mensaje.contains("45000")) {
            int idx = mensaje.indexOf("45000");
            String detalle = mensaje.substring(idx).replaceAll(".*MESSAGE_TEXT = '(.+?)'.*", "$1");
            return buildResponse(HttpStatus.BAD_REQUEST,
                    detalle.equals(mensaje) ? "Error de integridad en la base de datos" : detalle);
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de integridad en la base de datos");
    }

    // 500 - Error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
