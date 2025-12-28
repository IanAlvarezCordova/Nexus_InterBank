//ubi: src/main/java/com/ecusol/web/exception/GlobalExceptionHandler.java
package com.ecusol.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores de lógica (Credenciales, Validaciones)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage()); // Mensaje claro para el front

        // Si es error de credenciales, devolvemos 401, si no 400
        if (ex.getMessage().contains("Credenciales") || ex.getMessage().contains("bloqueado")) {
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Captura errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // Imprimir en consola para que tú lo veas
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor. Intente más tarde.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}