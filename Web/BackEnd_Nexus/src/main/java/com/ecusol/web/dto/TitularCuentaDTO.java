package com.ecusol.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TitularCuentaDTO {
    private String numeroCuenta;
    private String nombreCompleto;
    private String identificacionParcial;
    private String tipoCuenta;
}