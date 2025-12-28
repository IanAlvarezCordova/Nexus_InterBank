package com.estructura.cbs.mapper;

import com.estructura.cbs.dto.SucursalDTO;
import com.estructura.cbs.model.Sucursal;

public class SucursalMapper {

    public static Sucursal toEntity(SucursalDTO dto) {
        Sucursal s = new Sucursal();
        s.setSucursalId(dto.getSucursalId());
        s.setEntidadId(dto.getEntidadId());
        s.setUbicacionId(dto.getUbicacionId());
        s.setCodigoSucursal(dto.getCodigoSucursal());
        s.setNombre(dto.getNombre());
        s.setDireccion(dto.getDireccion());
        s.setTelefono(dto.getTelefono());
        s.setLatitud(dto.getLatitud());
        s.setLongitud(dto.getLongitud());
        s.setEstado(dto.getEstado());
        return s;
    }

    public static SucursalDTO toDTO(Sucursal s) {
        SucursalDTO dto = new SucursalDTO();
        dto.setSucursalId(s.getSucursalId());
        dto.setEntidadId(s.getEntidadId());
        dto.setUbicacionId(s.getUbicacionId());
        dto.setCodigoSucursal(s.getCodigoSucursal());
        dto.setNombre(s.getNombre());
        dto.setDireccion(s.getDireccion());
        dto.setTelefono(s.getTelefono());
        dto.setLatitud(s.getLatitud());
        dto.setLongitud(s.getLongitud());
        dto.setEstado(s.getEstado());
        return dto;
    }
}
