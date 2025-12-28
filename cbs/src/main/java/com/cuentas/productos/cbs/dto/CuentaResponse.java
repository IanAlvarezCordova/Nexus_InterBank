package com.cuentas.productos.cbs.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CuentaResponse {

    private Integer cuentaId;
    private String numeroCuenta;
    private Integer clienteId;
    private Integer tipoCuentaId;
    private Integer sucursalIdApertura;
    private BigDecimal saldo;
    private LocalDate fechaApertura;
    private String estado;

    public CuentaResponse() {}
}
