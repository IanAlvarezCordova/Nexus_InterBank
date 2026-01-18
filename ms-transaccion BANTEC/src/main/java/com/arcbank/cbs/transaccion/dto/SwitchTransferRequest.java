package com.arcbank.cbs.transaccion.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchTransferRequest {
    
    private UUID instructionId;
    
    private String bancoOrigen;      
    
    private String cuentaOrigen;     
    
    private String cuentaDestino;   
    
    private BigDecimal monto;
    
    private String moneda;          
    
    private String concepto;
}
