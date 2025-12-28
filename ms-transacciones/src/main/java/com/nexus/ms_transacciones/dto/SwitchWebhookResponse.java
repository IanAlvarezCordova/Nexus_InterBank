package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta del webhook al Switch.
 * El Switch espera: status="ACK", message, instructionId
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchWebhookResponse {
    private String status; // "ACK" para éxito, "NACK" para error
    private String message;
    private String instructionId;
}
