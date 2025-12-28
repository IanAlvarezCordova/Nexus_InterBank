//ubi: src/main/java/com/ecusol/ventanilla/dto/InfoCuentaDTO.java
package com.ecusol.ventanilla.dto;
import lombok.Data;

@Data
public class InfoCuentaDTO {
    private String numeroCuenta;
    private String nombreCompleto; // Coincide con TitularCuentaDTO del Core
    private String identificacionParcial;
    private String tipoCuenta;
    
    // Alias para el front (mapeo rápido)
    public String getTitular() { return nombreCompleto; }
    public String getTipo() { return tipoCuenta; }
}