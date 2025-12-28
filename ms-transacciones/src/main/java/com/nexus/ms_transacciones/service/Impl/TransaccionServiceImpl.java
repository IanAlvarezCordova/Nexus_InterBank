package com.nexus.ms_transacciones.service.Impl;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.client.SwitchClient;
import com.nexus.ms_transacciones.dto.*;
import com.nexus.ms_transacciones.mapper.TransaccionMapper;
import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import com.nexus.ms_transacciones.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository repository;
    private final CuentaClient cuentaClient;
    private final SwitchClient switchClient;
    private final TransaccionMapper mapper;

    @Override
    @Transactional
    public RespuestaTransferenciaDTO realizarTransferencia(SolicitudTransferenciaDTO solicitud) {

        // 1. Guardar Estado Inicial (PENDING)
        Transaccion tx = mapper.solicitudToEntity(solicitud);
        tx.setInstructionId(UUID.randomUUID().toString());
        if (tx.getReferencia() == null)
            tx.setReferencia(tx.getInstructionId());

        // FIX: Usar "idBancoDestino" que es el nombre correcto en la Entidad
        // Transaccion
        if (solicitud.getBancoDestinoId() != null) {
            tx.setIdBancoDestino(solicitud.getBancoDestinoId());
        } else {
            tx.setIdBancoDestino(1);
        }

        tx = repository.save(tx);

        try {
            // 2. PASO SAGA 1: Debito Local
            cuentaClient.debitar(tx.getCuentaOrigen(), tx.getMonto());

            // 3. DECISIÓN: ¿Interna o Externa?
            boolean esInterna = solicitud.esTransferenciaInterna();

            if (esInterna) {
                // --- TRANSFERENCIA INTERNA ---
                // Acreditar directamente en local
                cuentaClient.acreditar(tx.getCuentaDestino(), tx.getMonto());
                log.info("✅ Transferencia INTERNA completada: {} -> {}",
                        tx.getCuentaOrigen(), tx.getCuentaDestino());
            } else {
                // --- TRANSFERENCIA EXTERNA (DIGICONECU) ---
                String bancoDestino = solicitud.getBancoDestinoCodigo() != null
                        ? solicitud.getBancoDestinoCodigo()
                        : "BANTEC"; // Default si solo viene bancoDestinoId

                SwitchTransferRequest switchRequest = SwitchTransferRequest.builder()
                        .instructionId(tx.getInstructionId())
                        .bancoOrigen(switchClient.getBancoCodigo()) // NEXUS
                        .bancoDestino(bancoDestino)
                        .cuentaOrigen(tx.getCuentaOrigen())
                        .cuentaDestino(tx.getCuentaDestino())
                        .monto(tx.getMonto())
                        .moneda("USD")
                        .concepto(tx.getDescripcion() != null ? tx.getDescripcion() : "Transferencia interbancaria")
                        .build();

                SwitchTransferResponse response = switchClient.enviarTransferencia(switchRequest);

                if (response == null || !response.isSuccess()) {
                    String errorMsg = response != null ? response.getError() : "Sin respuesta del Switch";
                    throw new RuntimeException("Switch rechazó la transferencia: " + errorMsg);
                }

                log.info("✅ Transferencia INTERBANCARIA enviada al Switch: {} -> {}",
                        tx.getCuentaOrigen(), bancoDestino);
            }

            // Éxito
            tx.setEstado("COMPLETED");
            tx.setDescripcion("Transferencia Exitosa");
            tx.setFechaEjecucion(LocalDateTime.now());

        } catch (Exception e) {
            log.error(">>> SAGA FALLO GRAVE: {}", e.getMessage(), e);

            // 4. COMPENSACIÓN (Deshacer)
            if ("PENDING".equals(tx.getEstado())) {
                try {
                    // Solo compensamos si el dinero salió (si el error no fue SaldoInsuficiente)
                    if (!e.getMessage().contains("Fondos insuficientes")) {
                        log.info(">>> INICIANDO COMPENSACION para cuenta {}", tx.getCuentaOrigen());
                        cuentaClient.compensar(tx.getCuentaOrigen(), tx.getMonto());
                    }
                } catch (Exception exComp) {
                    log.error(">>> ERROR GRAVE: Fallo compensación manual", exComp);
                }
                tx.setEstado("FAILED");
                tx.setDescripcion("Error: " + e.getMessage());
            }
        }
        return mapper.entityToRespuestaDto(repository.save(tx));
    }

    @Override
    @Transactional
    public void procesarPagoEntrante(SwitchTransaccionDTO dto) {
        if (repository.existsByInstructionId(dto.getIdInstruccion())) {
            return; // Idempotencia: Ya la procesamos
        }

        Transaccion tx = mapper.switchDtoToEntity(dto);
        tx.setEstado("PENDING");
        tx = repository.save(tx);

        try {
            cuentaClient.acreditar(tx.getCuentaDestino(), tx.getMonto());
            tx.setEstado("COMPLETED");
            tx.setFechaEjecucion(LocalDateTime.now());
        } catch (Exception e) {
            tx.setEstado("FAILED");
            throw e; // Lanzamos error para que el Switch sepa que falló
        }
        repository.save(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerMovimientosPorCuenta(String numeroCuenta) {
        log.info("Consultando movimientos para cuenta: {}", numeroCuenta);
        List<Transaccion> transacciones = repository
                .findAllByCuentaOrigenOrCuentaDestinoOrderByFechaEjecucionDesc(numeroCuenta, numeroCuenta);

        return transacciones.stream().map(tx -> {
            MovimientoDTO dto = mapper.entityToMovimientoDto(tx);
            // Determinar rol en función del tipo de operación y origen/destino
            String tipo = tx.getTipo() != null ? tx.getTipo() : "TRANSFERENCIA";
            if ("DEPOSITO".equalsIgnoreCase(tipo)) {
                // Depósito: crédito para la cuenta origen
                dto.setRolTransaccion("RECEPTOR");
            } else if ("RETIRO".equalsIgnoreCase(tipo)) {
                // Retiro: débito para la cuenta origen
                dto.setRolTransaccion("EMISOR");
            } else {
                // Transferencia: depende si la cuenta es origen (débito) o destino (crédito)
                if (numeroCuenta.equals(tx.getCuentaOrigen())) {
                    dto.setRolTransaccion("EMISOR");
                } else {
                    dto.setRolTransaccion("RECEPTOR");
                }
            }
            return dto;
        }).toList();
    }
}