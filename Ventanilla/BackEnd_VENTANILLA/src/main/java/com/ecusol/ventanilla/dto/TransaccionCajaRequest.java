package com.ecusol.ventanilla.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransaccionCajaRequest {
    private String tipoOperacion;
    private String cuentaOrigen; 
    private String cuentaDestino; 
    private BigDecimal monto;
    private String descripcion;
}