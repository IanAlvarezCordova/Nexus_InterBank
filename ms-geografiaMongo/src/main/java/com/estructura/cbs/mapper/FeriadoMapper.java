package com.estructura.cbs.mapper;

import com.estructura.cbs.dto.FeriadoDTO;
import com.estructura.cbs.model.Feriado;

public class FeriadoMapper {

    public static Feriado toEntity(FeriadoDTO dto) {
        Feriado f = new Feriado();
        f.setFeriadoId(dto.getFeriadoId());
        f.setUbicacionId(dto.getUbicacionId());
        f.setFecha(dto.getFecha());
        f.setDescripcion(dto.getDescripcion());
        return f;
    }

    public static FeriadoDTO toDTO(Feriado f) {
        FeriadoDTO dto = new FeriadoDTO();
        dto.setFeriadoId(f.getFeriadoId());
        dto.setUbicacionId(f.getUbicacionId());
        dto.setFecha(f.getFecha());
        dto.setDescripcion(f.getDescripcion());
        return dto;
    }
}
