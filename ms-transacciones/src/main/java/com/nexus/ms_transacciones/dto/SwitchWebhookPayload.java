package com.nexus.ms_transacciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO para RECIBIR webhooks del Switch DIGICONECU.
 * El Switch envía este payload cuando otro banco transfiere a NEXUS.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchWebhookPayload {

    @JsonProperty("bancoOrigen")
    private String bancoOrigen;

    @JsonProperty("cuentaOrigen")
    private String cuentaOrigen;

    @JsonProperty("cuentaDestino")
    private String cuentaDestino;

    @JsonProperty("monto")
    private BigDecimal monto;

    @JsonProperty("referencia")
    private String referencia;

    @JsonProperty("concepto")
    private String concepto;
}
