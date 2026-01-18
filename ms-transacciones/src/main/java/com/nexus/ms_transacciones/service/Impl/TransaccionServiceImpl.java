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

        Transaccion tx = mapper.solicitudToEntity(solicitud);
        tx.setInstructionId(UUID.randomUUID().toString());
        if (tx.getReferencia() == null)
            tx.setReferencia(tx.getInstructionId());

        if (solicitud.getBancoDestinoId() != null) {
            tx.setIdBancoDestino(solicitud.getBancoDestinoId());
        } else {
            tx.setIdBancoDestino(1);
        }

        tx = repository.save(tx);

        try {
            cuentaClient.debitar(tx.getCuentaOrigen(), tx.getMonto());

            boolean esInterna = solicitud.esTransferenciaInterna();

            if (esInterna) {
                cuentaClient.acreditar(tx.getCuentaDestino(), tx.getMonto());
                log.info("✅ Transferencia INTERNA completada: {} -> {}",
                        tx.getCuentaOrigen(), tx.getCuentaDestino());
            } else {
                String bancoDestino = solicitud.getBancoDestinoCodigo() != null
                        ? solicitud.getBancoDestinoCodigo()
                        : "BANTEC";

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
                        .name("Cliente Nexus") 
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
                        .bancoOrigen(switchClient.getBancoCodigo())
                        .bancoDestino(bancoDestino)
                        .cuentaOrigen(tx.getCuentaOrigen())
                        .cuentaDestino(tx.getCuentaDestino())
                        .monto(tx.getMonto())
                        .moneda("USD")
                        .concepto(tx.getDescripcion() != null ? tx.getDescripcion() : "Transferencia interbancaria")
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

                log.info("✅ Transferencia INTERBANCARIA enviada al Switch: {} -> {}",
                        tx.getCuentaOrigen(), bancoDestino);
            }

            tx.setEstado("COMPLETED");
            tx.setDescripcion("Transferencia Exitosa");
            tx.setFechaEjecucion(LocalDateTime.now());

        } catch (Exception e) {
            log.error(">>> SAGA FALLO GRAVE: {}", e.getMessage(), e);

            if ("PENDING".equals(tx.getEstado())) {
                try {
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
            return;
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
            throw e; 
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

            if (numeroCuenta.equals(tx.getCuentaOrigen())) {
                dto.setRolTransaccion("EMISOR");
            } else {
                dto.setRolTransaccion("RECEPTOR"); 
            }
            return dto;
        }).toList();
    }
}