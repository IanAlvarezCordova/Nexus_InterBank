package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transacciones/callback")
@RequiredArgsConstructor
@Slf4j
public class CallbackController {

    private final TransaccionRepository repository;

    @PostMapping
    public ResponseEntity<Void> recibirConfirmacion(@RequestBody Map<String, Object> payload) {
        String txId = (String) payload.get("transaccionId");
        String estado = (String) payload.get("estado");
        String mensaje = (String) payload.get("mensaje");

        log.info("Callback recibido para TX {}: Estado={}", txId, estado);

        repository.findByInstructionId(txId).ifPresent(tx -> {
            if ("PENDING".equals(tx.getEstado())) {
                tx.setEstado("COMPLETED".equals(estado) ? "COMPLETED" : "FAILED");
                tx.setDescripcion(mensaje);
                repository.save(tx);
                log.info("Estado de transacción local actualizado.");
            }
        });

        return ResponseEntity.ok().build();
    }
}