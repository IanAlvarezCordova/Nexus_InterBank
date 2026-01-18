package com.arcbank.cbs.transaccion.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcbank.cbs.transaccion.service.TransaccionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/transacciones/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final TransaccionService transaccionService;

    @PostMapping
    public ResponseEntity<?> recibirTransferenciaEntrante(@RequestBody Map<String, Object> payload) {
        log.info("📥 Webhook recibido del switch: {}", payload);

        try {
            String instructionId = payload.get("instructionId") != null
                    ? payload.get("instructionId").toString()
                    : null;
            String cuentaDestino = payload.get("cuentaDestino") != null
                    ? payload.get("cuentaDestino").toString()
                    : null;
            String bancoOrigen = payload.get("bancoOrigen") != null
                    ? payload.get("bancoOrigen").toString()
                    : "DESCONOCIDO";

            BigDecimal monto = BigDecimal.ZERO;
            if (payload.get("monto") != null) {
                monto = new BigDecimal(payload.get("monto").toString());
            }

            if (instructionId == null || cuentaDestino == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Webhook con datos incompletos: {}", payload);
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "NACK",
                        "error", "Datos incompletos en el webhook"));
            }

            transaccionService.procesarTransferenciaEntrante(
                    instructionId, cuentaDestino, monto, bancoOrigen);

            log.info("✅ Transferencia entrante procesada: {} -> cuenta {}", bancoOrigen, cuentaDestino);

            return ResponseEntity.ok(Map.of(
                    "status", "ACK",
                    "message", "Transferencia procesada exitosamente",
                    "instructionId", instructionId));

        } catch (Exception e) {
            log.error("❌ Error procesando webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(422).body(Map.of(
                    "status", "NACK",
                    "error", e.getMessage()));
        }
    }
}
