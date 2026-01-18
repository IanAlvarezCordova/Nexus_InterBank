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

    @Operation(summary = "Recibir transferencia entrante desde otro banco via Switch")
    @PostMapping("/webhook")
    public ResponseEntity<SwitchWebhookResponse> recibirTransferenciaEntrante(
            @RequestBody com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO payload) {

        String instructionId = payload.getBody().getInstructionId();

        log.info("📥 Webhook ISO 20022 recibido: ID {} | Origen {} -> Destino {}",
                instructionId,
                payload.getHeader().getOriginatingBankId(),
                payload.getBody().getCreditor().getAccountId());

        try {
            if (payload.getReferencia() != null &&
                    repository.existsByInstructionId(payload.getReferencia())) {
                log.warn("⚠️ Transferencia duplicada ignorada: {}", payload.getReferencia());
                return ResponseEntity.ok(new SwitchWebhookResponse(
                        "ACK",
                        "Transferencia ya procesada previamente",
                        instructionId));
            }

            cuentaClient.acreditar(payload.getCuentaDestino(), payload.getMonto());

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

    @Operation(summary = "Obtener lista de bancos del ecosistema DIGICONECU")
    @GetMapping("/bancos")
    public ResponseEntity<List<BancoDTO>> obtenerBancos() {
        List<BancoDTO> bancos = switchClient.obtenerBancos();
        return ResponseEntity.ok(bancos);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ms-transacciones",
                "banco", switchClient.getBancoCodigo()));
    }
}