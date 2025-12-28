package com.nexus.ms_transacciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO para ENVIAR transferencias al Switch DIGICONECU.
 * Formato esperado por POST /api/v2/transfers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchTransferRequest {

    @JsonProperty("instructionId")
    private String instructionId;

    @JsonProperty("bancoOrigen")
    private String bancoOrigen;

    @JsonProperty("bancoDestino")
    private String bancoDestino;

    @JsonProperty("cuentaOrigen")
    private String cuentaOrigen;

    @JsonProperty("cuentaDestino")
    private String cuentaDestino;

    @JsonProperty("monto")
    private BigDecimal monto;

    @JsonProperty("moneda")
    private String moneda;

    @JsonProperty("concepto")
    private String concepto;
}
