package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.client.SwitchClient;
import com.nexus.ms_transacciones.dto.BancoDTO;
import com.nexus.ms_transacciones.dto.IsoMensajeDTO;
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
            @RequestBody IsoMensajeDTO payload) {

        if (payload == null || payload.getBody() == null || payload.getHeader() == null) {
            log.error("Webhook recibido con payload nulo o incompleto");
            return ResponseEntity.badRequest()
                    .body(new SwitchWebhookResponse("NACK", "Estructura ISO inválida", null));
        }

        String instructionId = payload.getBody().getInstructionId();
        String originatingBank = payload.getHeader().getOriginatingBankId();

        String cuentaDestino = null;
        if (payload.getBody().getCreditor() != null) {
            cuentaDestino = payload.getBody().getCreditor().getAccountId();
        }

        String endToEndId = payload.getBody().getEndToEndId();

        log.info("Webhook ISO 20022 recibido: ID {} | Origen {} -> Destino {}",
                instructionId, originatingBank, cuentaDestino);

        if (cuentaDestino == null) {
            return ResponseEntity.badRequest()
                    .body(new SwitchWebhookResponse("NACK", "Cuenta destino no informada", instructionId));
        }

        try {
            if (repository.existsByInstructionId(instructionId)) {
                log.warn("⚠️ Transferencia duplicada ignorada: {}", instructionId);
                return ResponseEntity.ok(new SwitchWebhookResponse(
                        "ACK",
                        "Transferencia ya procesada previamente",
                        instructionId));
            }

            java.math.BigDecimal monto = payload.getBody().getAmount().getValue();

            cuentaClient.acreditar(cuentaDestino, monto);

            Transaccion tx = new Transaccion();
            tx.setInstructionId(instructionId);
            tx.setReferencia(endToEndId);

            String cuentaOrigen = "EXTERNA";
            if (payload.getBody().getDebtor() != null && payload.getBody().getDebtor().getAccountId() != null) {
                cuentaOrigen = payload.getBody().getDebtor().getAccountId();
            }
            tx.setCuentaOrigen(cuentaOrigen);

            tx.setCuentaDestino(cuentaDestino);
            tx.setMonto(monto);
            tx.setDescripcion("Transferencia recibida de " + originatingBank + " - " +
                    payload.getBody().getRemittanceInformation());
            tx.setEstado("COMPLETED");
            tx.setRolTransaccion("CREDITO");
            tx.setFechaEjecucion(LocalDateTime.now());

            repository.save(tx);

            log.info("✅ Transferencia acreditada exitosamente en cuenta {}", cuentaDestino);

            return ResponseEntity.ok(new SwitchWebhookResponse(
                    "ACK",
                    "Transferencia procesada exitosamente",
                    instructionId));

        } catch (Exception e) {
            log.error("Error procesando webhook: {}", e.getMessage());
            return ResponseEntity.status(422).body(new SwitchWebhookResponse(
                    "NACK",
                    "Error de negocio: " + e.getMessage(),
                    instructionId));
        }
    }

    @Operation(summary = "Recibir confirmación de devolucion (Return) desde Switch")
    @PostMapping({ "/webhook/api/incoming/return", "/api/incoming/return" }) // Catch-all for weird path concatenation
    public ResponseEntity<SwitchWebhookResponse> recibirDevolucionEntrante(@RequestBody IsoMensajeDTO payload) {
        log.info(">>>> WEBHOOK DEVOLUCION RECIBIDO: {}", payload);

        try {
            // Logic to process the return credit (reverse of debit)
            // Extract IDs
            String instructionId = payload.getBody().getInstructionId();
            String originalInstructionId = payload.getBody().getOriginalInstructionId(); // Assuming field exists or we
                                                                                         // parse logic
            // Note: IsoBody in our DTO might not have originalInstructionId directly if
            // it's the same class as transfer.
            // Check ReturnRequestDTO vs IsoMensajeDTO.
            // If the switch sends a pacs.004 wrapped in the same structure:

            String targetAccount = payload.getBody().getCreditor().getAccountId();
            java.math.BigDecimal amount = payload.getBody().getAmount().getValue();

            // Creditar la cuenta (reverso)
            log.info("Procesando reverso/devolución para cuenta: {}", targetAccount);
            cuentaClient.acreditar(targetAccount, amount);

            // Guardar o actualizar transacción
            Transaccion tx = new Transaccion();
            tx.setInstructionId(instructionId);
            tx.setReferencia(
                    originalInstructionId != null ? originalInstructionId : "REF-RET-" + System.currentTimeMillis());
            tx.setCuentaDestino(targetAccount);
            tx.setMonto(amount);
            tx.setTipo("C"); // Credito
            tx.setDescripcion("Devolución Recibida: " + payload.getBody().getRemittanceInformation());
            tx.setEstado("COMPLETED");
            tx.setFechaEjecucion(LocalDateTime.now());
            repository.save(tx);

            return ResponseEntity.ok(new SwitchWebhookResponse("ACK", "Devolución procesada", instructionId));

        } catch (Exception e) {
            log.error("Error procesando devolución: {}", e.getMessage());
            return ResponseEntity.ok(new SwitchWebhookResponse("NACK", "Error: " + e.getMessage(), "unknown"));
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