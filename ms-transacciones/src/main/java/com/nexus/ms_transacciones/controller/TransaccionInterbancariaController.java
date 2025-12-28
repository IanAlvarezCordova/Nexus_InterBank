package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.client.SwitchClient;
import com.nexus.ms_transacciones.dto.BancoDTO;
import com.nexus.ms_transacciones.dto.SwitchWebhookPayload;
import com.nexus.ms_transacciones.dto.SwitchWebhookResponse;
import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@Tag(name = "Switch DIGICONECU", description = "Endpoints para comunicación con el Switch Interbancario")
@Slf4j
public class TransaccionInterbancariaController {

    private final TransaccionRepository repository;
    private final CuentaClient cuentaClient;
    private final SwitchClient switchClient;

    /**
     * Webhook que recibe transferencias entrantes desde el Switch DIGICONECU.
     * Formato esperado por el Switch.
     */
    @Operation(summary = "Recibir transferencia entrante desde otro banco via Switch")
    @PostMapping("/webhook")
    public ResponseEntity<SwitchWebhookResponse> recibirTransferenciaEntrante(
            @RequestBody SwitchWebhookPayload payload) {
        log.info("📥 Webhook recibido desde {}: {} -> {} por ${}",
                payload.getBancoOrigen(),
                payload.getCuentaOrigen(),
                payload.getCuentaDestino(),
                payload.getMonto());

        try {
            // 1. Verificar idempotencia (no procesar duplicados)
            if (payload.getReferencia() != null &&
                    repository.existsByInstructionId(payload.getReferencia())) {
                log.warn("⚠️ Transferencia duplicada ignorada: {}", payload.getReferencia());
                return ResponseEntity.ok(new SwitchWebhookResponse(
                        "ACK",
                        "Transferencia ya procesada previamente",
                        payload.getReferencia()));
            }

            // 2. Acreditar la cuenta destino
            cuentaClient.acreditar(payload.getCuentaDestino(), payload.getMonto());

            // 3. Registrar la transacción entrante
            Transaccion tx = new Transaccion();
            tx.setInstructionId(payload.getReferencia());
            tx.setReferencia(payload.getReferencia());
            tx.setCuentaOrigen(payload.getCuentaOrigen());
            tx.setCuentaDestino(payload.getCuentaDestino());
            tx.setMonto(payload.getMonto());
            tx.setDescripcion(payload.getConcepto() != null ? payload.getConcepto()
                    : "Transferencia recibida de " + payload.getBancoOrigen());
            tx.setEstado("COMPLETED");
            tx.setRolTransaccion("CREDITO");
            tx.setFechaEjecucion(LocalDateTime.now());
            repository.save(tx);

            log.info("✅ Transferencia acreditada exitosamente en cuenta {}", payload.getCuentaDestino());

            return ResponseEntity.ok(new SwitchWebhookResponse(
                    "ACK",
                    "Transferencia procesada exitosamente",
                    payload.getReferencia()));

        } catch (Exception e) {
            log.error("❌ Error procesando webhook: {}", e.getMessage());
            return ResponseEntity.status(422).body(new SwitchWebhookResponse(
                    "NACK",
                    "Error: " + e.getMessage(),
                    payload.getReferencia()));
        }
    }

    /**
     * Obtiene la lista de bancos disponibles en el ecosistema DIGICONECU.
     * El frontend usa esto para mostrar el combo de bancos destino.
     */
    @Operation(summary = "Obtener lista de bancos del ecosistema DIGICONECU")
    @GetMapping("/bancos")
    public ResponseEntity<List<BancoDTO>> obtenerBancos() {
        List<BancoDTO> bancos = switchClient.obtenerBancos();
        return ResponseEntity.ok(bancos);
    }

    /**
     * Health check del servicio de transacciones.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ms-transacciones",
                "banco", switchClient.getBancoCodigo()));
    }
}