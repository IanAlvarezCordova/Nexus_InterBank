package com.cuentas.productos.cbs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoCuentaRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String estado;

    @NotBlank
    private String tipoAmortizacion;

    public TipoCuentaRequest() {}
}
