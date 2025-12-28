package com.cuentas.productos.cbs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoCuentaResponse {

    private Integer tipoCuentaId;
    private String nombre;
    private String descripcion;
    private String estado;
    private String tipoAmortizacion;

    public TipoCuentaResponse() {}
}
