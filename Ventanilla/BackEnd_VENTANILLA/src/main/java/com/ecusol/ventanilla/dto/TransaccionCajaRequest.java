package com.ecusol.ventanilla.dto;
import lombok.Data;
import java.math.BigDecimal;

// Este DTO debe ser IDÉNTICO al del Core para que el JSON coincida
@Data
public class TransaccionCajaRequest {
    private String tipoOperacion;
    private String cuentaOrigen;  // En Core se llama cuentaOrigen
    private String cuentaDestino; // En Core se llama cuentaDestino
    private BigDecimal monto;
    private String descripcion;
}