package com.estructura.cbs.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SucursalDTO {

    private Integer sucursalId;
    private Integer entidadId;
    private Integer ubicacionId;
    private String codigoSucursal;
    private String nombre;
    private String direccion;
    private String telefono;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String estado;

    public SucursalDTO() {}

    public SucursalDTO(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }
}
