package com.cuentas.productos.cbs.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TasaInteresResponse {

    private Integer tasaInteresHistoricoId;
    private Integer tipoCuentaId;
    private Double tasaMensual;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;

    public TasaInteresResponse() {}
}
