package com.estructura.cbs.mapper;

import com.estructura.cbs.dto.UbicacionGeograficaDTO;
import com.estructura.cbs.model.UbicacionGeografica;

public class UbicacionMapper {

    public static UbicacionGeografica toEntity(UbicacionGeograficaDTO dto) {
        UbicacionGeografica u = new UbicacionGeografica();
        u.setUbicacionId(dto.getUbicacionId());
        u.setNombre(dto.getNombre());
        u.setTipoUbicacion(dto.getTipoUbicacion());
        u.setUbicacionPadreId(dto.getUbicacionPadreId());
        return u;
    }

    public static UbicacionGeograficaDTO toDTO(UbicacionGeografica u) {
        UbicacionGeograficaDTO dto = new UbicacionGeograficaDTO();
        dto.setUbicacionId(u.getUbicacionId());
        dto.setNombre(u.getNombre());
        dto.setTipoUbicacion(u.getTipoUbicacion());
        dto.setUbicacionPadreId(u.getUbicacionPadreId());
        return dto;
    }
}