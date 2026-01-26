package com.nexus.ms_transacciones.service;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.dto.IsoMensajeDTO;
import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionAsyncConsumer {

    private final TransaccionRepository repository;
    private final CuentaClient cuentaClient;
    private final RestTemplate restTemplate;

    @RabbitListener(queues = "${banco.cola.entrada}")
    public void recibirTransferenciaRabbit(IsoMensajeDTO payload) {
        String instructionId = payload.getBody().getInstructionId();
        log.info("⚡ RabbitMQ: Recibida transferencia ID: {}", instructionId);

        String estadoFinal = "COMPLETED";
        String mensajeFinal = "Transferencia procesada con éxito";

        try {
            if (repository.existsByInstructionId(instructionId)) {
                log.warn("Duplicado detectado en cola: {}", instructionId);
                return; 
            }
            String cuentaDestino = payload.getBody().getCreditor().getAccountId();
            java.math.BigDecimal monto = payload.getBody().getAmount().getValue();
            
            cuentaClient.acreditar(cuentaDestino, monto);

            Transaccion tx = new Transaccion();
            tx.setInstructionId(instructionId);
            tx.setReferencia(payload.getBody().getEndToEndId());
            tx.setCuentaOrigen(payload.getBody().getDebtor().getAccountId());
            tx.setCuentaDestino(cuentaDestino);
            tx.setMonto(monto);
            tx.setEstado("COMPLETED");
            tx.setFechaEjecucion(LocalDateTime.now());
            tx.setRolTransaccion("CREDITO");
            
            repository.save(tx);
            log.info("Acreditación exitosa.");

        } catch (Exception e) {
            log.error("Error procesando mensaje de cola: {}", e.getMessage());
            estadoFinal = "FAILED";
            mensajeFinal = e.getMessage();
        }

        enviarWebhookCallback(payload.getHeader().getCallbackUrl(), instructionId, estadoFinal, mensajeFinal);
    }

    private void enviarWebhookCallback(String urlCallback, String txId, String estado, String mensaje) {
        if (urlCallback == null || urlCallback.isEmpty()) {
            log.warn("No hay URL de Callback para notificar la transacción {}", txId);
            return;
        }

        log.info("Enviando Webhook a: {}", urlCallback);

        Map<String, Object> response = Map.of(
            "transaccionId", txId,
            "estado", estado, 
            "mensaje", mensaje,
            "fechaProcesamiento", LocalDateTime.now().toString()
        );

        try {
            restTemplate.postForEntity(urlCallback, response, Void.class);
            log.info("Webhook entregado correctamente.");
        } catch (Exception e) {
            log.error("Fallo al entregar Webhook al origen: {}", e.getMessage());
        }
    }
}