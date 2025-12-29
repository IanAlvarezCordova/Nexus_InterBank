//ubi: src/main/java/com/ecusol/web/dto/SucursalDTO.java
package com.ecusol.web.dto;

import lombok.Data;
import java.math.BigDecimal;

// Espejo del DTO del Core
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