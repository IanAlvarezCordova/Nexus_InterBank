package com.cuentas.productos.cbs.mapper;

import com.cuentas.productos.cbs.dto.TasaInteresRequest;
import com.cuentas.productos.cbs.dto.TasaInteresResponse;
import com.cuentas.productos.cbs.model.TasaInteresHistorico;

public class TasaInteresMapper {

    private TasaInteresMapper() {}

    public static TasaInteresHistorico toEntity(TasaInteresRequest req) {
        TasaInteresHistorico entity = new TasaInteresHistorico();
        entity.setTasaMensual(req.getTasaMensual());
        entity.setFechaInicioVigencia(req.getFechaInicioVigencia());
        entity.setFechaFinVigencia(req.getFechaFinVigencia());
        return entity;
    }

    public static TasaInteresResponse toResponse(TasaInteresHistorico entity) {
        TasaInteresResponse dto = new TasaInteresResponse();
        dto.setTasaInteresHistoricoId(entity.getId());
        dto.setTipoCuentaId(entity.getTipoCuenta().getId());
        dto.setTasaMensual(entity.getTasaMensual());
        dto.setFechaInicioVigencia(entity.getFechaInicioVigencia());
        dto.setFechaFinVigencia(entity.getFechaFinVigencia());
        return dto;
    }
}
