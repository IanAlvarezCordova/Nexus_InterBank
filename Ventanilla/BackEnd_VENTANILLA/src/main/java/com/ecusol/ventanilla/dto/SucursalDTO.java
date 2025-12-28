package com.ecusol.ventanilla.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SucursalDTO {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private BigDecimal lat;
    private BigDecimal lng;
}