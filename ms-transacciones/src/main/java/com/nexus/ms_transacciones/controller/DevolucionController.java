package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class DevolucionController {

    private final TransaccionService transaccionService;

    @PostMapping("/{id}/devolucion")
    public ResponseEntity<?> iniciarDevolucion(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            if (motivo == null || motivo.isBlank()) {
                return ResponseEntity.badRequest().body("Motivo es requerido");
            }

            transaccionService.iniciarDevolucion(id, motivo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); // Log stack trace
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
