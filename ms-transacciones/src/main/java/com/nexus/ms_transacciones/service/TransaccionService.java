package com.nexus.ms_transacciones.service;

import com.nexus.ms_transacciones.dto.*;
import java.util.List;

public interface TransaccionService {
    RespuestaTransferenciaDTO realizarTransferencia(SolicitudTransferenciaDTO solicitud);

    void procesarPagoEntrante(SwitchTransaccionDTO dto);

    List<MovimientoDTO> obtenerMovimientosPorCuenta(String numeroCuenta);
}