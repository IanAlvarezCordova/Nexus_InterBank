package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchWebhookResponse {
    @com.fasterxml.jackson.annotation.JsonAlias("estado")
    private String status;

    private String message;

    @com.fasterxml.jackson.annotation.JsonAlias("idInstruccion")
    private String instructionId;
}
