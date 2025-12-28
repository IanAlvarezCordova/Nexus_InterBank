package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Integer transaccionId;
    private String referencia;
    private String rolTransaccion; // "DEBITO" o "CREDITO"
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fechaEjecucion;
    private String cuentaOrigen;
    private String cuentaDestino;
    // Tipo funcional: DEPOSITO, RETIRO, TRANSFERENCIA
    private String tipo;
}
