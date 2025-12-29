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
     * Formato esperado por el Switch: ISO 20022
     */
    @Operation(summary = "Recibir transferencia entrante desde otro banco via Switch (ISO 20022)")
    @PostMapping("/webhook")
    public ResponseEntity<SwitchWebhookResponse> recibirTransferenciaEntrante(
            @RequestBody com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO payload) {

        String instructionId = payload.getBody().getInstructionId();

        log.info("📥 Webhook ISO 20022 recibido: ID {} | Origen {} -> Destino {}",
                instructionId,
                payload.getHeader().getOriginatingBankId(),
                payload.getBody().getCreditor().getAccountId());

        try {
            // 1. Verificar idempotencia (no procesar duplicados)
            if (repository.existsByInstructionId(instructionId)) {
                log.warn("⚠️ Transferencia duplicada ignorada: {}", instructionId);
                return ResponseEntity.ok(new SwitchWebhookResponse(
                        "ACK",
                        "Transferencia ya procesada previamente",
                        instructionId));
            }

            // 2. Acreditar la cuenta destino
            String cuentaDestino = payload.getBody().getCreditor().getAccountId();
            java.math.BigDecimal monto = payload.getBody().getAmount().getValue();

            cuentaClient.acreditar(cuentaDestino, monto);

            // 3. Registrar la transacción entrante
            Transaccion tx = new Transaccion();
            tx.setInstructionId(instructionId);
            tx.setReferencia(payload.getBody().getEndToEndId());
            tx.setCuentaOrigen(payload.getBody().getDebtor().getAccountId());
            tx.setCuentaDestino(cuentaDestino);
            tx.setMonto(monto);
            tx.setDescripcion("Transferencia recibida de " + payload.getHeader().getOriginatingBankId());
            tx.setEstado("COMPLETED");
            tx.setRolTransaccion("CREDITO");
            tx.setFechaEjecucion(java.time.LocalDateTime.now());
            // Guardar ID banco origen si es posible, o dejar null por ahora

            repository.save(tx);

            log.info("✅ Transferencia acreditada exitosamente en cuenta {}", cuentaDestino);

            return ResponseEntity.ok(new SwitchWebhookResponse(
                    "ACK",
                    "Transferencia procesada exitosamente",
                    instructionId));

        } catch (Exception e) {
            log.error("❌ Error procesando webhook: {}", e.getMessage());
            return ResponseEntity.status(422).body(new SwitchWebhookResponse(
                    "NACK",
                    "Error: " + e.getMessage(),
                    instructionId));
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