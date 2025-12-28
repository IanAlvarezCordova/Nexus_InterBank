package com.estructura.cbs.mapper;

import com.estructura.cbs.dto.EntidadDTO;
import com.estructura.cbs.model.Entidad;

public class EntidadMapper {

    public static Entidad toEntity(EntidadDTO dto) {
        Entidad e = new Entidad();
        e.setEntidadId(dto.getEntidadId());
        e.setRuc(dto.getRuc());
        e.setRazonSocial(dto.getRazonSocial());
        e.setNombreComercial(dto.getNombreComercial());
        e.setEstado(dto.getEstado());
        return e;
    }

    public static EntidadDTO toDTO(Entidad e) {
        EntidadDTO dto = new EntidadDTO();
        dto.setEntidadId(e.getEntidadId());
        dto.setRuc(e.getRuc());
        dto.setRazonSocial(e.getRazonSocial());
        dto.setNombreComercial(e.getNombreComercial());
        dto.setEstado(e.getEstado());
        return dto;
    }
}
