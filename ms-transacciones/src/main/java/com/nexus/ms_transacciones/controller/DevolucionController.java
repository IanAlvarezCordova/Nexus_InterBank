package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class DevolucionController {

    private final TransaccionService transaccionService;

    @PostMapping("/{id}/devolucion")
    public ResponseEntity<Void> iniciarDevolucion(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String motivo = body.get("motivo");
        if (motivo == null || motivo.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        transaccionService.iniciarDevolucion(id, motivo);
        return ResponseEntity.ok().build();
    }
}
