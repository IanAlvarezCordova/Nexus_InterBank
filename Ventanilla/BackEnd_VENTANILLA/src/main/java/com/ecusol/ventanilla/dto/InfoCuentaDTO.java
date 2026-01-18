package com.ecusol.ventanilla.dto;
import lombok.Data;

@Data
public class InfoCuentaDTO {
    private String numeroCuenta;
    private String nombreCompleto; 
    private String identificacionParcial;
    private String tipoCuenta;
    
    public String getTitular() { return nombreCompleto; }
    public String getTipo() { return tipoCuenta; }
}