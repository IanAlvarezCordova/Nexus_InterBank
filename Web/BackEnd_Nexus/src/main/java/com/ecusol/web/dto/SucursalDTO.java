//ubi: src/main/java/com/ecusol/web/dto/SucursalDTO.java
package com.ecusol.web.dto;
import lombok.Data;
import java.math.BigDecimal;

// Espejo del DTO del Core
@Data
public class SucursalDTO {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private BigDecimal lat;
    private BigDecimal lng;
}