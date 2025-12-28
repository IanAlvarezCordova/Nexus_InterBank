package com.cuentas.productos.cbs.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CrearCuentaRequest {

    @NotNull
    private Integer clienteId;

    @NotNull
    private Integer tipoCuentaId;

    @NotNull
    private Integer sucursalIdApertura;

    @PositiveOrZero
    private BigDecimal saldoInicial;

    public CrearCuentaRequest() {
    }

    public CrearCuentaRequest(Integer clienteId,
                              Integer tipoCuentaId,
                              Integer sucursalIdApertura,
                              BigDecimal saldoInicial) {
        this.clienteId = clienteId;
        this.tipoCuentaId = tipoCuentaId;
        this.sucursalIdApertura = sucursalIdApertura;
        this.saldoInicial = saldoInicial;
    }

    @Override
    public String toString() {
        return "CrearCuentaRequest{" +
                "clienteId=" + clienteId +
                ", tipoCuentaId=" + tipoCuentaId +
                ", sucursalIdApertura=" + sucursalIdApertura +
                ", saldoInicial=" + saldoInicial +
                '}';
    }
}
