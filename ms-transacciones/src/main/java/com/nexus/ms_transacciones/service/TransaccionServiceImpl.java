package com.nexus.ms_transacciones.service;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.client.SwitchClient;
import com.nexus.ms_transacciones.dto.*;
import com.nexus.ms_transacciones.mapper.TransaccionMapper;
import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import java.time.format.DateTimeFormatter;
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
            // 1. Débito Local
            cuentaClient.debitar(tx.getCuentaOrigen(), tx.getMonto());

            boolean esInterna = solicitud.esTransferenciaInterna();

            if (esInterna) {
                // Transferencia Interna (Mismo Banco)
                cuentaClient.acreditar(tx.getCuentaDestino(), tx.getMonto());
                log.info(" Transferencia INTERNA completada: {} -> {}",
                        tx.getCuentaOrigen(), tx.getCuentaDestino());
            } else {
                // 2. Transferencia Interbancaria (ISO 20022 via Switch)
                String bancoDestino = solicitud.getBancoDestinoCodigo() != null
                        ? solicitud.getBancoDestinoCodigo()
                        : "BANTEC"; // Default si no viene

                // Construcción del DTO Unificado (IsoMensajeDTO)
                IsoMensajeDTO isoRequest = new IsoMensajeDTO();

                // HEADER
                isoRequest.setHeader(IsoMensajeDTO.IsoHeader.builder()
                        .messageId("MSG-" + System.currentTimeMillis())
                        .creationDateTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .originatingBankId(switchClient.getBancoCodigo())
                        .build());

                // BODY
                isoRequest.setBody(IsoMensajeDTO.IsoBody.builder()
                        .instructionId(tx.getInstructionId())
                        .endToEndId(tx.getReferencia())
                        .remittanceInformation(tx.getDescripcion() != null ? tx.getDescripcion() : "Transferencia SPI")

                        // Amount
                        .amount(IsoMensajeDTO.IsoAmount.builder()
                                .currency("USD")
                                .value(tx.getMonto())
                                .build())

                        // Debtor (Ordenante)
                        .debtor(IsoMensajeDTO.IsoDebtor.builder()
                                .name("Cliente Nexus")
                                .accountId(tx.getCuentaOrigen())
                                .accountType("CHECKING")
                                .build())

                        // Creditor (Beneficiario)
                        .creditor(IsoMensajeDTO.IsoCreditor.builder()
                                .name("Cliente Externo")
                                .accountId(tx.getCuentaDestino())
                                .accountType("SAVINGS")
                                .targetBankId(bancoDestino) // CRÍTICO para el enrutamiento del Switch
                                .build())
                        .build());

                // 3. Envío al Switch
                SwitchWebhookResponse response = switchClient.enviarTransferencia(isoRequest);

                if (response == null || "NACK".equals(response.getStatus())) {
                    throw new RuntimeException("Switch rechazó la operación: "
                            + (response != null ? response.getMessage() : "Null Response"));
                }

                log.info(" Transferencia INTERBANCARIA enviada al Switch: {} -> {}",
                        tx.getCuentaOrigen(), bancoDestino);
            }

            tx.setEstado("COMPLETED");
            tx.setDescripcion("Transferencia Exitosa");
            tx.setFechaEjecucion(LocalDateTime.now());

        } catch (Exception e) {
            log.error(">>> SAGA FALLO GRAVE: {}", e.getMessage(), e);

            // Rollback manual (Compensación)
            if ("PENDING".equals(tx.getEstado())) {
                try {
                    if (!e.getMessage().contains("Fondos insuficientes")) {
                        log.info(">>> INICIANDO COMPENSACION para cuenta {}", tx.getCuentaOrigen());
                        cuentaClient.compensar(tx.getCuentaOrigen(), tx.getMonto());
                    }
                } catch (Exception exComp) {
                    log.error(">>> ERROR GRAVE: Fallo compensación manual", exComp);
                }
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.length() > 255) {
                    errorMsg = errorMsg.substring(0, 255);
                }
                tx.setEstado("FAILED");
                tx.setDescripcion("Error: " + errorMsg);
                tx.setMensajeError(e.getMessage().length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage());
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

            // Manual mapping patch and debug
            dto.setInstructionId(tx.getInstructionId());
            // log.info("Tx: {}, InstID Entity: {}, InstID DTO: {}", tx.getTransaccionId(),
            // tx.getInstructionId(), dto.getInstructionId());

            if (numeroCuenta.equals(tx.getCuentaOrigen())) {
                dto.setRolTransaccion("EMISOR");
            } else {
                dto.setRolTransaccion("RECEPTOR");
            }
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void iniciarDevolucion(UUID idInstruccion, String motivo) {
        log.info("🔄 Iniciando DEVOLUCIÓN MANUAL para transacción {}", idInstruccion);

        Transaccion txOriginal = repository.findByInstructionId(idInstruccion.toString())
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        // Validar que sea una transacción recibida y completada
        if (!"COMPLETED".equals(txOriginal.getEstado())) {
            throw new RuntimeException("Solo se pueden devolver transacciones COMPLETADAS");
        }

        // Construir pacs.004
        com.nexus.ms_transacciones.dto.ReturnRequestDTO returnRequest = new com.nexus.ms_transacciones.dto.ReturnRequestDTO();

        com.nexus.ms_transacciones.dto.ReturnRequestDTO.Header header = new com.nexus.ms_transacciones.dto.ReturnRequestDTO.Header();
        header.setMessageId("MSG-RET-" + UUID.randomUUID().toString().substring(0, 8));
        header.setCreationDateTime(LocalDateTime.now().toString());
        // OriginatingBankId debe ser MI banco (porque yo estoy devolviendo)
        // Pero en Transacciones "Received", el origenEra "OTRO" y destino "YO".
        // Al devolver, YO soy el originating de la devolución.
        header.setOriginatingBankId(switchClient.getBancoCodigo());
        returnRequest.setHeader(header);

        com.nexus.ms_transacciones.dto.ReturnRequestDTO.Body body = new com.nexus.ms_transacciones.dto.ReturnRequestDTO.Body();
        // originalInstructionId es el ID de la transacción original
        // PERO OJO: En la entidad Transaccion, ¿instructionId es el que vino del
        // switch?
        // En procesarPagoEntrante: tx.setInstructionId(dto.getIdInstruccion()); -> Sí.
        body.setOriginalInstructionId(txOriginal.getInstructionId());
        body.setReturnReason(motivo);

        com.nexus.ms_transacciones.dto.ReturnRequestDTO.ReturnAmount amount = new com.nexus.ms_transacciones.dto.ReturnRequestDTO.ReturnAmount();
        // Asumimos moneda USD siempre o la sacamos de la TX si tuviera campo moneda (no
        // lo vi en entity simple, asumo USD)
        amount.setCurrency("USD");
        amount.setValue(txOriginal.getMonto());
        body.setReturnAmount(amount);

        returnRequest.setBody(body);

        // Enviar
        switchClient.enviarDevolucion(returnRequest);

        // Actualizar estado local
        txOriginal.setEstado("RETURN_REQUESTED"); // O "REFUNDED"
        txOriginal.setDescripcion("Devolución iniciada: " + motivo);
        repository.save(txOriginal);
    }

    @Transactional
    public void guardarTransaccionVentanilla(VentanillaDTO.TransaccionCajaRequest request, String usuarioId,
            String referencia) {
        Transaccion tx = new Transaccion();
        tx.setInstructionId(UUID.randomUUID().toString());
        tx.setReferencia(referencia != null ? referencia : tx.getInstructionId());
        tx.setCuentaOrigen(request.getCuentaOrigen());
        tx.setCuentaDestino(request.getCuentaDestino());
        tx.setMonto(request.getMonto());
        tx.setDescripcion(request.getDescripcion());
        tx.setEstado("COMPLETED");
        tx.setRolTransaccion(request.getTipoOperacion());
        tx.setFechaEjecucion(LocalDateTime.now());
        tx.setIdUsuario(usuarioId);

        repository.save(tx);
        log.info("Transacción ventanilla guardada: {}", tx.getInstructionId());
    }
}