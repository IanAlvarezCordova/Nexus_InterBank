package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchWebhookResponse {
    private String status; 
    private String message;
    private String instructionId;
}
