package com.estructura.cbs.dto;

import lombok.Data;

@Data
public class UbicacionGeograficaDTO {

    private Integer ubicacionId;
    private String nombre;
    private String tipoUbicacion;
    private Integer ubicacionPadreId;

    public UbicacionGeograficaDTO() {}

    public UbicacionGeograficaDTO(Integer ubicacionId) {
        this.ubicacionId = ubicacionId;
    }
}
