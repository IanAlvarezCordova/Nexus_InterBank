package com.nexus.ms_transacciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(name = "SwitchData", description = "Estructura de datos compatible con ISO-8583 requerida por el Switch")
public class SwitchTransaccionDTO {

    @JsonProperty("IdInstruccion")
    @Schema(description = "UUID único para control de idempotencia", example = "550e8400-e29b-41d4-a716-446655440000")
    private String idInstruccion;

    @JsonProperty("EndToEnd")
    @Schema(description = "Referencia de trazabilidad punto a punto", example = "REF-ECU-9988")
    private String endToEnd;

    @JsonProperty("IdBancoOrigen")
    @Schema(description = "ID asignado a Ecuasol", example = "2")
    private Integer idBancoOrigen;

    @JsonProperty("IdBancoDestino")
    @Schema(description = "ID del banco receptor", example = "1")
    private Integer idBancoDestino;

    @JsonProperty("CuentaOrigen")
    @Schema(description = "Cuenta debitada", example = "2200112233")
    private String cuentaOrigen;

    @JsonProperty("CuentaDestino")
    @Schema(description = "Cuenta a acreditar", example = "4050607080")
    private String cuentaDestino;

    @JsonProperty("Monto")
    @Schema(description = "Valor a transferir", example = "150.75")
    private BigDecimal monto;

    @JsonProperty("Mensaje")
    @Schema(description = "Descripción técnica o motivo", example = "Pago de servicios")
    private String mensaje;

    @JsonProperty("EstadoActual")
    @Schema(description = "Estado dentro del Switch", example = "PENDING", allowableValues = {"PENDING", "COMPLETED", "FAILED"})
    private String estadoActual;
}