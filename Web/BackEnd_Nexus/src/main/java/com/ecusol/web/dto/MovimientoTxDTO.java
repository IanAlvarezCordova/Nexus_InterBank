package com.ecusol.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoTxDTO(
        Integer transaccionId,
        String referencia,
        String rolTransaccion,
        BigDecimal monto,
        String descripcion,
        LocalDateTime fechaEjecucion,
        String cuentaOrigen,
        String cuentaDestino,
        String tipo 
) {}
