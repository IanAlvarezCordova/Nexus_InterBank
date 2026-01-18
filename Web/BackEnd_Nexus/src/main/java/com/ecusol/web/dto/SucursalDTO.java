package com.ecusol.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SucursalDTO {
    @com.fasterxml.jackson.annotation.JsonAlias("sucursalId")
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;

    @com.fasterxml.jackson.annotation.JsonAlias("latitud")
    private BigDecimal lat;

    @com.fasterxml.jackson.annotation.JsonAlias("longitud")
    private BigDecimal lng;
}