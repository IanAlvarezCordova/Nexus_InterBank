package com.ecusol.ventanilla.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CuentaResumenDTO {
    private Integer cuentaId;
    private String numeroCuenta;
    private BigDecimal saldo;
    private String estado;
    private Integer tipoCuentaId;
    private String tipo; 

    public String getTipo() {
        if (tipo != null)
            return tipo;
        if (tipoCuentaId == null)
            return "Cuenta";
        return tipoCuentaId == 1 ? "Ahorros" : "Corriente";
    }
}