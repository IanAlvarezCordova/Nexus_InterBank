package com.nexus.ms_transacciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta del Switch después de enviar una transferencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchTransferResponse {

    @JsonProperty("data")
    private TransferData data;

    @JsonProperty("error")
    private String error;

    @JsonProperty("success")
    private boolean success;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferData {
        private String instructionId;
        private String estado;
        private String bancoOrigen;
        private String bancoDestino;
        private Double monto;
        private String timestamp;
    }
}
