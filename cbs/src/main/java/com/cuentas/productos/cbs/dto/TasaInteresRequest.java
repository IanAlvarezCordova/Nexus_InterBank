package com.cuentas.productos.cbs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TasaInteresRequest {

    @NotNull
    private Double tasaMensual;

    @NotNull
    private LocalDate fechaInicioVigencia;

    private LocalDate fechaFinVigencia;

    public TasaInteresRequest() {}
}
