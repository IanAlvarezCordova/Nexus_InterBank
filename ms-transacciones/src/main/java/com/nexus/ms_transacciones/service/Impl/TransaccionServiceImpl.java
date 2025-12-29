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
                        : "BANTEC"; // Default

                // CONSTRUCCION ISO 20022
                com.nexus.ms_transacciones.dto.iso.IsoHeaderDTO header = com.nexus.ms_transacciones.dto.iso.IsoHeaderDTO
                        .builder()
                        .messageId("MSG-" + System.currentTimeMillis())
                        .creationDateTime(java.time.Instant.now().toString())
                        .originatingBankId(switchClient.getBancoCodigo())
                        .build();

                com.nexus.ms_transacciones.dto.iso.IsoAmountDTO amount = com.nexus.ms_transacciones.dto.iso.IsoAmountDTO
                        .builder()
                        .currency("USD")
                        .value(tx.getMonto())
                        .build();

                com.nexus.ms_transacciones.dto.iso.IsoAccountDTO debtor = com.nexus.ms_transacciones.dto.iso.IsoAccountDTO
                        .builder()
                        .name("Cliente Nexus") // Idealmente obtener nombre real
                        .accountId(tx.getCuentaOrigen())
                        .accountType("CHECKING")
                        .build();

                com.nexus.ms_transacciones.dto.iso.IsoAccountDTO creditor = com.nexus.ms_transacciones.dto.iso.IsoAccountDTO
                        .builder()
                        .name("Cliente Externo")
                        .accountId(tx.getCuentaDestino())
                        .accountType("SAVINGS")
                        .targetBankId(bancoDestino)
                        .build();

                com.nexus.ms_transacciones.dto.iso.IsoBodyDTO body = com.nexus.ms_transacciones.dto.iso.IsoBodyDTO
                        .builder()
                        .instructionId(tx.getInstructionId())
                        .endToEndId("REF-" + tx.getInstructionId())
                        .amount(amount)
                        .debtor(debtor)
                        .creditor(creditor)
                        .build();

                com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO isoRequest = com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO
                        .builder()
                        .header(header)
                        .body(body)
                        .build();

                com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO response = switchClient
                        .enviarTransferencia(isoRequest);

                if (response == null) {
                    throw new RuntimeException("Sin respuesta del Switch");
                }

                // Si llegamos aquí es 200/201 OK porque RestTemplate lanza excepción en 4xx/5xx
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