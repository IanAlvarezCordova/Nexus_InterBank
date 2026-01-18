package com.ecusol.ventanilla.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class VentanillaOpDTO {
    private String tipoOperacion; 
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private BigDecimal monto;
    private String descripcion;
}