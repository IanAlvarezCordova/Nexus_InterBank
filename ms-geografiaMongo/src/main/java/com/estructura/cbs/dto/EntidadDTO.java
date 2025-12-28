package com.estructura.cbs.dto;

import lombok.Data;

@Data
public class EntidadDTO {

    private Integer entidadId;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String estado;

    public EntidadDTO() {}

    public EntidadDTO(Integer entidadId) {
        this.entidadId = entidadId;
    }
}
