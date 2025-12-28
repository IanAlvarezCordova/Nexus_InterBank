package com.cuentas.productos.cbs.mapper;

import com.cuentas.productos.cbs.dto.TipoCuentaRequest;
import com.cuentas.productos.cbs.dto.TipoCuentaResponse;
import com.cuentas.productos.cbs.model.TipoCuenta;

public class TipoCuentaMapper {

    private TipoCuentaMapper() {}

    public static TipoCuenta toEntity(TipoCuentaRequest req) {
        TipoCuenta entity = new TipoCuenta();
        entity.setNombre(req.getNombre());
        entity.setDescripcion(req.getDescripcion());
        entity.setEstado(req.getEstado());
        entity.setTipoAmortizacion(req.getTipoAmortizacion());
        return entity;
    }

    public static TipoCuentaResponse toResponse(TipoCuenta entity) {
        TipoCuentaResponse dto = new TipoCuentaResponse();
        dto.setTipoCuentaId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setEstado(entity.getEstado());
        dto.setTipoAmortizacion(entity.getTipoAmortizacion());
        return dto;
    }

    public static void updateEntity(TipoCuenta entity, TipoCuentaRequest req) {
        entity.setNombre(req.getNombre());
        entity.setDescripcion(req.getDescripcion());
        entity.setEstado(req.getEstado());
        entity.setTipoAmortizacion(req.getTipoAmortizacion());
    }
}
