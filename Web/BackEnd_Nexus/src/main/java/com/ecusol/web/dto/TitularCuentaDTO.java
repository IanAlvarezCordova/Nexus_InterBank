package com.ecusol.web.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitularCuentaDTO {
    private String numeroCuenta;
    private String nombreCompleto;
    private String identificacionParcial;
    private String tipoCuenta;
}